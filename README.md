## Testing Rebalancing of Large Consumer Groups

The Kafka eco-system is built for horizontal scalability allow real time processing of extremely large data volumes. 
On the cluster side, the number of brokers are often scaled up to 50 nodes, while the number of partitions per broker currently should not be higher than 4000. 
On the client side, many producers instances can write to the same topic without problems, since producers can mostly work independently of each other. 

When it comes to consumers, Kafka provides the concept of consumer groups to horizontally distribute event processing between a number of consumer instances within the group. 
One partition can only be consumed by a single consumer instance within a group. 
When consumer instances take some time to process indivdiual messages, and when the overall message throughput for an input topic is large, then the group must be composed of a large number of consumer instances. 

When consumer instances join or leave a group, a consumer group rebalancing is initiated, which means that the partitions of a subscribed topic are re-destributed among the instances of the consumer group. 
There are different strategies for partitions assignment, implemented by the so-called RangeAssignor, RoundRobinAssingor, StickyAssignor and CooperativeStickyAssignor, respectively. 
More strategies can be plugged in by implementing the ConsumerPartitionAssignor interface. 

Rebalancing duration and increase in latency will depend on the message throughput, the number of instances in a consumer group, the message size, the partition assignment strategy, the load on the consumer group coordinater, possibly also on the version of Apache Kafka. 
This project serves as a playground for trying out different values for these parameters, such that we can get an idea of what impact a rebalancing will have on the overall message processing, in particular on the consumer lag.

This project features a near real world producer and consumer application configured to run with a Confluent Cloud cluster. 
For ease of scalability of the prodcuer and consumer instances, Kubernetes deployment files are provided to deploy and scale the applications on a Kubernetes cluster. 
Note that for a production near scenario, the producer and consumer applications should be physically close to the Confluent Cloud cluster, ideally in the same data center. 

### Building and Running The Project

* Adjust the configuration values within `demo-producer/deployment.yaml` and `demo-consumer/deployment.yaml` to suit your test case. 
* You can adjust more producer and consumer configurations within the `application.properties` files of the producer and consumer applications. 
* Create a sample topic with a sufficient number of partitions: `./create-topic-ccloud.sh 300`. 
  You will need the confluent cloud cli for this to work. 
  Alternatively you can create the topic via the Confluent Cloud Web UI. 
* Build the producer: `cd demo-producer; mvn clean package -Dmaven.test.skip=true`
* Build the consumer: `cd demo-consumer; mvn clean package -Dmaven.test.skip=true`
* Build and deploy the images to Google Container Registry (this should be similarly easy on Azure and AWS): 
  * `cd demo-producer; cloud-build.sh`
  * `cd demo-consumer; cloud-build.sh`
* Deploy the applications to your kubernetes cluster: 
  * `cd demo-producer` 
  * adjust the path to your image within the `deployment.yaml` file
  * `kubectl apply -f deployment.yaml`
  * `cd ../demo-consumer` 
  * adjust the path to your image and the nubmer of consumer instances within the `deployment.yaml` file
  * `kubeclt apply -f deployment.yaml`
* Once your deployment is up and running, use the Confluent Cloud Web UI, to see the consumer lag within the consumer-lag overview tab. 
* restart single instance of your consumer group or do a rolling restart of the entire consumer group. 
* You may want to try out different partition assigment strategies, and experiment with different consumer settings, such as `max.poll.records` to avoid busy consumers drop out of the group. 

* Don't forget to clean up once you are done with the experiments: 
  * 'cd demo-producer; kubectl delete -f deployment.yaml'
  * 'cd demo-consumer; kubectl delete -f deployment.yaml'
  * './delete-topic-ccloud.sh'

This demo was tested with 300 consumer group instances on a shared Confluent Cloud cluster. 
Rolling restarts of the consumer group took about 2 minutes with the RangeAssignor and StickyAssignor. 

### Trying Incremental Cooperative Rebalancing locally. 

Kafka Cooperative Incremental Rebalancing avoids unnecessary revocations of partitions from Kafka consumer instances. This is in contrast to the tradditional management of partition assignments with the RangeAssignor, which is enabled by default. This project helps to better understand the differences by trying things out. 

![Screenshot](docs/screenshot.png)

### Running with Confluent Cloud

This is the easiest solution to get the demo running. No need to setup your own cluster. 

#### Prerequisites

* login to Confluent Cloud: Get a free account at https://confluent.cloud
* created an Api-key and api-secret for your account
* `mvn` command line tool 
* JDK 11 or higher
* Access to Maven Central

#### Starting the Demo

* create the `test-topic` via the UI with a few partitions
* take a look at the `application-cloud.properties` file and notice that IncrementalCooperativeRebalancing has been enabled for the Kafka consumers. 
* Insert your api-key and api-secret in this file. 
* Open a couple of terminal windows (3 will do). 
* In each terminal window start the application one after the other and watch the logs: `export SPRING_PROFILES_ACTIVE=cloud; mvn spring-boot:run` . 
* Open a couple of terminal windows (3 will do). 
* In each terminal window start the application one after the other and watch the logs: `export SPRING_PROFILES_ACTIVE=dockercompose; mvn spring-boot:run` . 
  You should see that only those partition reassignments are executed which are really 
  needed:

```
    now assigned partitions: test-topic-3, test-topic-1, test-topic-8, test-topic-6
    compare with previously owned partitions: test-topic-3, test-topic-8, test-topic-6
    newly added partitions: test-topic-1
    revoked partitions:
```

* Now edit application-cloud.properties and comment out the line that configures the CooperativeStickyAssignor. The default RangeAssignor will be used instead. 
* Stop all your application instances, and start them again. Watch the logs and see that all partitions are revoked upon each change of group membership in the consumer group. 

### Running with Docker-compose

#### Prerequisites

* `mvn` command line tool
* JDK 11 or later
* `kafka-topics` command line tool
* access to maven central for downloading dependencies 
* docker and docker-compose
* access to docker-hub for downloading the docker images

#### Starting the demo

* Start the Kafka cluster: `docker-compose up -d`. This will start a 3 broker, 3 Zookeeper cluster. 
* Adjust your `/etc/hosts` file to resolve kafka-1, kafka-2, kafka-3 to localhost: 

   127.0.0.1 kafka-1
   127.0.0.1 kafka-2
   127.0.0.1 kafka-3

* Create a test topic: `./create-topic.sh`
* take a look at the `application-dockercompose.properties` file and notice that IncrementalCooperativeRebalancing has been enabled for the Kafka consumers. 
* Open a couple of terminal windows (3 will do). 
* In each terminal window start the application one after the other and watch the logs: `export SPRING_PROFILES_ACTIVE=dockercompose; mvn spring-boot:run` . 
  You should see that only those partition reassignments are executed which are really 
  needed:

```
    now assigned partitions: test-topic-3, test-topic-1, test-topic-8, test-topic-6
    compare with previously owned partitions: test-topic-3, test-topic-8, test-topic-6
    newly added partitions: test-topic-1
    revoked partitions:
```

* Now edit application-dockercompose.properties and comment out the line that configures the CooperativeStickyAssignor. The default RangeAssignor will be used instead. 
* Stop all your application instances, and start them again. Watch the logs and see that all partitions are revoked upon each change of group membership in the consumer group. 
