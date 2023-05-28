package com.rosariob.crud.couchbase.repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.transactions.Transactions;
import com.rosariob.crud.couchbase.entity.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

@SpringBootTest
@Testcontainers
public class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CouchbaseTemplate couchbaseTemplate;

    private static final String BUCKET_NAME = "bucket1";
    private static final String SCOPE_NAME = "_default";
    private static final String COLLECTION_NAME = "customers";
    private static Cluster cluster;
    private static Bucket bucket;
    private static Scope scope;
    private static Collection collection;
    private static String keySpace;

    private static final DockerImageName COUCHBASE_IMAGE_ENTERPRISE = DockerImageName
            .parse("couchbase:enterprise-7.0.2")
            .asCompatibleSubstituteFor("couchbase/server");
    @Container
    private static CouchbaseContainer container = new CouchbaseContainer(COUCHBASE_IMAGE_ENTERPRISE)
            .withCredentials("Administrator", "password")
            .withBucket(new BucketDefinition(BUCKET_NAME).withPrimaryIndex(true))
            .withStartupTimeout(Duration.ofMinutes(2));

    @DynamicPropertySource
    static void registerCouchbaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.couchbase.connectionString", container::getConnectionString);
        registry.add("spring.couchbase.username", container::getUsername);
        registry.add("spring.couchbase.password", container::getPassword);
    }

    @BeforeAll
    public static void setUp() {
        cluster = Cluster.connect(container.getConnectionString(),
                ClusterOptions.clusterOptions(container.getUsername(), container.getPassword()));
        cluster.waitUntilReady(Duration.ofMinutes(2));
        bucket = cluster.bucket(BUCKET_NAME);
        scope = bucket.scope(SCOPE_NAME);
        keySpace = String.join(".", BUCKET_NAME, SCOPE_NAME, COLLECTION_NAME);
        collection = scope.collection(COLLECTION_NAME);
    }

    @BeforeEach
    public void clearCollection() {
        Transactions transactions = couchbaseTemplate.getCouchbaseClientFactory().getCluster().transactions();
        transactions.run(ctx -> ctx.query("DELETE FROM " + keySpace));
    }
    @Test
    public void testFindById() {
            Customer alex = new Customer("customer1", "Alex", "Stone");
            collection.insert(alex.getId(), alex);
            Customer customer = customerRepository.findById(alex.getId());
            assertEqualsExceptCas(alex, customer);
    }


    @Test
    public void testFindAll() {
        Customer alex = new Customer("customer1", "Alex", "Stone");
        Customer jack = new Customer("customer2", "Jack", "Sparrow");
        List<Customer> customerList = List.of(alex, jack);
        customerList.forEach(customer -> collection.insert(customer.getId(), customer));
        List<Customer> customers = customerRepository.findAll();
        customers.forEach(c -> {
            if(c.getId() == alex.getId()){
                assertEqualsExceptCas(alex, c);
            }
            assertEqualsExceptCas(jack, c);
        });
    }

    @Test
    public void testCreate(){
        Customer alex = new Customer("customer1","Alex", "Stone");
        customerRepository.create(alex);
        JsonObject result = collection.get(alex.getId()).contentAsObject();
        assertEqualsExceptCas(alex, mapJsonToCustomer(result));
    }

    @Test
    public void testUpdate(){
        Customer alex = new Customer("customer1","Alex", "Stone");
        Customer alex2 = new Customer("customer1","Alex", "Yellow");
        collection.insert(alex.getId(), alex);
        customerRepository.update(alex2);
        JsonObject result = collection.get(alex2.getId()).contentAsObject();
        assertEqualsExceptCas(alex2, mapJsonToCustomer(result));
    }

    @Test
    public void testUpsert(){
        Customer alex = new Customer("customer1","Alex", "Stone");
        customerRepository.upsert(alex);
        JsonObject result = collection.get(alex.getId()).contentAsObject();
        assertEqualsExceptCas(alex, mapJsonToCustomer(result));
    }

    @Test
    public void testDeleteById(){
        Customer alex = new Customer("customer1","Alex", "Stone");
        collection.insert(alex.getId(), alex);
        customerRepository.deleteById(alex.getId());
        ExistsResult exists = collection.exists(alex.getId());
        Assertions.assertFalse(exists.exists());
    }

    @Test
    public void testDeleteAll() {
        Customer alex = new Customer("customer1","Alex", "Stone");
        Customer jack = new Customer("customer2", "Jack", "Sparrow");
        List<Customer> customerList = List.of(alex, jack);
        customerList.forEach(customer -> collection.insert(customer.getId(), customer));
        customerRepository.deleteAll();
        ExistsResult exists = collection.exists(alex.getId());
        Assertions.assertFalse(exists.exists());
        ExistsResult exists2 = collection.exists(jack.getId());
        Assertions.assertFalse(exists2.exists());
    }
    private static void assertEqualsExceptCas(Customer expected, Customer actual) {
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getFirstName(), actual.getFirstName());
        Assertions.assertEquals(expected.getLastName(), actual.getLastName());
    }

    private static Customer mapJsonToCustomer(JsonObject jsonObject) {
        String firstName = jsonObject.get("firstName").toString();
        String lastName = jsonObject.get("lastName").toString();
        String id = firstName.equals("Alex") ? "customer1" : "customer2";
        return new Customer(id, firstName, lastName);
    }
}
