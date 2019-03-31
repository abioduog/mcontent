# Overview #

Purpose of this system is to allow end-users to subscribe to SMS delivery lists whose content is designed by content providers.

### High-level view? ###

The SMS delivered to the end-user contains content-provider designed short message and a URL to the content-provider designed content.

### Delivery pipe and scheduling ###

(Delivery list = delivery pipe.)

The delivery pipes are grouped into services. End-user makes subscriptions to services, and thus can be part of multiple delivery pipes simultaneously.

Delivery pipes contain content-entries that are the actual deliverables. Currently a delivery pipe can be designed to dispatch it's content entries via calendar-type scheduling or series-type scheduling. Series-type scheduling means, that after an end-user has subscribed to said delivery pipe, one content entry will be delivered to the end-user with each successive day.

Each content in a delivery pipe can be approved/disapproved, and only after it is, will it be part of scheduled deliverable.


### Type of delivery pipe ###

The content-designer chooses a type for a delivery-pipe when creating it. Currently supported types are default and comic. Default means that each deliverable has a short message and a URL to a free-form HTML. Comic means that each deliverable has a short message and a URL to one image.


# Developping #
## Build WAR ##
$ docker run -it --rm --name mcontent-maven -v ${pwd}/.m2:/root/.m2 -v ${pwd}:/usr/src/mcontent -w /usr/src/mcontent maven:3.3-jdk-8 mvn clean verify
## Run the system up ##
$ docker-compose up