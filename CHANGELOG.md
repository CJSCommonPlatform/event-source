# Change Log
All notable changes to this project will be documented in this file, which follows the guidelines
on [Keep a CHANGELOG](http://keepachangelog.com/). This project adheres to
[Semantic Versioning](http://semver.org/).

## [Unreleased]

## [7.2.0-M5] - 2020-11-09
### Changed
- Test release to test the migration from `https://travis-ci.org/` to `https://travis-ci.com/` 

## [7.2.0-M4] - 2020-11-04
### Changed
- Moved timer bean utilities to framework-libraries
- Updated framework to version 7.2.0-M4

## [7.2.0-M3] - 2020-11-03
### Changed
- Updated framework to version 7.2.0-M3

## [7.2.0-M2] - 2020-11-02
### Changed
- Updated framework to version 7.2.0-M2

## [7.2.0-M1] - 2020-10-29
### Added
- Added support for FeatureControl toggling by annotating service component
handler methods with @FeatureControl
### Changed
- Updated framework to version 7.2.0-M1

## [7.1.4] - 2020-10-16
### Changed
- Updated framework-libraries to version 7.1.5
    - Builders of generated pojos now have a `withValuesFrom(...)` method 
to allow the builder to be initialised with the values of another pojo instance  

## [7.1.3] - 2020-10-15
### Changed
- Security updates to apache.tika, commons.beanutils, commons.guava and junit in common-bom
- Updated common-bom to 7.1.1

## [7.1.2] - 2020-10-09
### Changed
- The context name is now used for creating JMS destination name if the event
 is an administration event

## [7.1.1] - 2020-09-25
### Changed
- Updated framework-libraries to version 7.1.1

## [7.1.0] - 2020-09-23
### Changed
- Updated parent maven-framework-parent-pom to version 2.0.0
- Updated framework to version 7.1.0
- Moved to new Cloudsmith.io repository for hosting maven artifacts
- Updated encrypted properties in travis.yaml to point to cloudsmith


## [7.0.10] - 2020-09-10
### Changed
- Update framework-libraries to 7.0.11

## [7.0.9] - 2020-08-28
### Changed
- Changed the removal of the event_log trigger to be called by a 
ServletContextListener to fix call happening after database 
connections destroyed


## [7.0.8] - 2020-08-14
### Changed
- Updated framework to 7.0.10

## [7.0.7] - 2020-08-13
### Changed
- Updated framework to 7.0.9

## [7.0.6] - 2020-07-24
### Added
- indexes added to stream_id and position_in_stream in event_log table 
### Changed
- DefaultEventStoreDataSourceProvider changed to a singleton, 
so the caching of the DataSource works properly
- Test util class DatabaseCleaner has an additional method 
'cleanEventStoreTables(...)' for truncating specified tables 
in the event-store
- Update framework to 7.0.8

## [7.0.5] - 2020-07-08
### Changed
- Update framework to 7.0.7
- Updated travis.yaml security setting for bintray to use the new cjs user

## [7.0.4] - 2020-06-03
### Changed
- Update framework to 7.0.6

## [7.0.3] - 2020-05-29
### Changed
- Update framework to 7.0.5

## [7.0.2] - 2020-05-27
### Changed
- Update framework to 7.0.4

## [7.0.1] - 2020-05-22
### Removed
- jboss-ejb3-ext-api from dependency-management

## [7.0.0] - 2020-05-22
### Changed
- Changed dependencies on only depend on framework 7.0.3
- Bumped version to 7.0.0 to match other framework libraries

## [2.4.13] - 2020-04-23
### Changed
- Update framework to 6.4.2

## [2.4.12] - 2020-04-23
### Failed Release
- Github issues

## [2.4.11] - 2020-04-14
### Added
- Added a test DataSource for the file-service database

## [2.4.10] - 2020-04-09
### Added
- Added indexes to processed_event table

### Changed
- microservice-framework -> 6.4.1

## [2.4.9] - 2020-03-04
### Changed
- Fail cleanly if exception occurs while accessing subscription event source

## [2.4.8] - 2020-01-29
### Changed
- Inserts into the event-buffer no longer fails if there is a conflict; it just logs a warning

## [2.4.7] - 2020-01-24
### Changed
- Event store now works with multiple event sources
- Event store now compatible with contexts that do not have a command pillar
- Extracted all command pillar SystemCommands into their own module

## [2.4.6] - 2020-01-21
### Added
- Catchup for multiple components now run in order of component and subscription priority
- Added event source name to catchup logger output
### Fixed
- Fixed catchup error where catchup was marked as complete after all subscriptions rather than all components

## [2.4.5] - 2020-01-06
### Removed
- Remove mechanism to also drop/add trigger on SUSPEND/UNSUSPEND as it causes 
many strange ejb database errors

## [2.4.4] - 2020-01-06
### Added
- Added mechanism to also drop/add trigger to event_log table on SUSPEND/UNSUSPEND commands
### Fixed
- Fixed potential problem of a transaction failing during catchup causing catchup to never complete

## [2.4.3] - 2019-12-06
### Changed
- Backpressure added to the event processing queues during catchup
### Fixed
- Verification completion log message now correctly logs if verification of Catchup or of Rebuild

## [2.4.2] - 2019-11-25
### Added
- Catchup Log message to show that all active events are now waiting to be consumed

## [2.4.1] - 2019-11-20
### Changed
- Now batch inserting PublishedEvents on rebuild to speed up the command
- Changed batch size on PublishedEvent rebuild to 1,000 per batch

## [2.4.0] - 2019-11-13
### Added
 New SystemCommand VERIFY_REBUILD to verify the results of of the rebuild 
    - Verifies that the number of active events in event_log matches the number of events in published_event
    - Verifies that each event_number in published_event correctly links to an existing previous_event 
    - Verifies that each active stream has at least one event 
### Changed
- SHUTTER command renamed to SUSPEND
- UNSHUTTER command renamed to UNSUSPEND
- The database trigger for publishing on the event_log table is now added on application 
startup and removed on application shut down
- Updated to framework 6.4.0 

## [2.3.1] - 2019-11-07
### Fixed
- removed rogue logging of payload during event validation

## [2.3.0] - 2019-11-07
### Added
- Added event_id to the processed_event table to aid debugging of publishing
### Changed
- Event-Store SystemCommands moved into this project to break the dependency on framework

## [2.2.7] - 2019-11-04
### Added
- New command 'ValidatePublishedEventsCommand' and handler for validating all events in 
event_log against their schemas
### Changed
- Updated framework to 6.2.5
- Updated Json Schema Catalog to 1.7.6

## [2.2.6] - 2019-10-30
### Changed
- Improved the event_log query to determine if the renumber of events is complete. 
Changed to use select MAX rather than count(*)

## [2.2.5] - 2019-10-29
### Changed
- Removed asynchronous bean to run catchup queue and replaced with ManagedExecutor

## [2.2.4] - 2019-10-28
### Fixed
- During catchup each event is now processed in a separate transaction

## [2.2.3] - 2019-10-25
### Fixed
- Catchup range processing

## [2.2.2] - 2019-10-24
### Changed
- Pre publish and publish timer beans now run in a separate thread.
- New JNDI boolean values of 'pre.publish.disable' and 'publish.disable' to disable
the running of PrePublisherTimerBean and PublisherTimerBean respectively
- Error message of event linking verification now gives more accurate error messages
based on whether the problem is in published_event or processed_event
- New SystemCommands EnablePublishingCommand and DisablePublishingCommand for enabling/disabling the publishing beans
- Catchup will check for all missing events in the processed_event table and Catchup only the missing event ranges

## [2.2.1] - 2019-10-18
### Fixed
- VERIFY_CATCHUP now correctly marks its status as COMMAND_FAILED if any of 
the verification steps fail. Verification warnings are considered successful  

## [2.2.0] - 2019-10-15
### Added
- New table in System database 'system_command_status' for storing state of commands
### Changed
- Updated framework to 6.2.0
- All system commands now store their state in the system_command_status table in the system 
database. This is to allow the JMX client to wait until the command has completed or failed 
before it exits.
- Now using CatchupCommand to determine if we are running Event or Indexer catchup
- Converted ShutteringExecutors to use the new ShutteringExecutor interface
- Added commandId to all SystemEvents
- All SystemCommand handlers now take a mandatory UUID, commandId.
- Moved MdcLogger to framework 'jmx-command-handling' module

## [2.1.2] - 2019-10-01
### Changed
- Updated to framework 6.1.1
- All SystemCommand handlers now take a mandatory UUID, commandId.

## [2.1.1] - 2019-09-28
### Added
- New system event 'CatchupProcessingOfEventFailedEvent' fired if processing of any PublishedEvent during catchup fails
### Changed
- All system events moved into their own module 'event-store-management-events'
- Unsuccessful catchups now logged correctly in catchup completion.

## [2.1.0] - 2019-09-26
### Changed
- Subscriptions are no longer run asynchronously during catchup. Change required for MI catchup.
- Event catchup and Indexer catchup now run the same code

## [2.0.25] - 2019-10-08
### Fixed
- Fix single event rebuilding of published_event table

## [2.0.24] - 2019-10-07
### Fixed
- Fix issue where more than 1000 inactive events stops the rebuild process

### Changed
Run the renumbering of events in a batch

## [2.0.23] - 2019-10-04
### Changed
- Updated framework to 6.0.17  

## [2.0.22] - 2019-09-24
### Changed
- Catchup verification logging now runs in MDC context 

## [2.0.21] - 2019-09-23
### Added
- New SystemCommand to verify the results of running catchup 
    - Verifies that the number of active events in event_log matches the number of events in published_event
    - Verifies that the number of events in published_event matches the number of events in processed_event
    - Verifies that the stream_buffer table is empty
    - Verifies that each event_number in published_event correctly links to an existing previous_event 
    - Verifies that each event_number in processed_event correctly links to an existing previous_event 
    - Verifies that each active stream has at least one event 

## [2.0.20] - 2019-09-23
### Fixed
- Fixed name of publish queue trigger 

## [2.0.19] - 2019-09-19
### Added
- New SystemCommands AddTrigger and RemoveTrigger to manage the trigger on the event_log table 
- MDC logging of the service context for JMX commands

## [2.0.18] - 2019-09-18
### Changed
- Use DefaultEnvelopeProvider in MetadataEventNumberUpdater directly to fix classloading errors during rebuild
- Add logging to catchup processing, log every 1000th event
- Moved the conversion of PublishedEvent to JsonObject for the publishing of catchup inside the multi-threaded code

## [2.0.17] - 2019-09-16
### Changed
- Use DefaultJsonEnvelopeProvider in EventConverted directly to fix classloading errors during rebuild
- The event-source-name is now always calculated from the name of the event and never from its source field

## [2.0.16] - 2019-09-13
### Changed
- Renumbering of events during rebuild is now run in batches to allow shorter transactions

## [2.0.15] - 2019-09-11
### Changed
- Process rebuild in pages of events
- Changed transaction type of EventCatchupProcessorBean to NEVER to fix timeouts for long running transactions
- Reduced the maximum runtime for each iteration of the publishing beans to 450 milliseconds
- Long running transaction during rebuild broken into separate transactions
- Update framework to 6.0.14

## [2.0.14] - 2019-09-08
### Changed
- Update framework to 6.0.12

## [2.0.13] - 2019-09-06
### Changed
- Events are now renumbered according to their date_created order during rebuild

## [2.0.12] - 2019-09-04
### Changed
- If the event source name in the JsonEnvelope is missing is calculated from the event name rather 
than throwing an exception

## [2.0.11] - 2019-08-30
### Changed
- Update framework to 6.0.11

## [2.0.10] - 2019-08-28
### Changed
- Update framework to 6.0.10

## [2.0.9] - 2019-08-21
### Changed
- Update framework to 6.0.9

## [2.0.8] - 2019-08-21
### Changed
- Update framework to 6.0.8

## [2.0.7] - 2019-08-20
### Changed
- Update framework to 6.0.7

## [2.0.6] - 2019-08-19
### Changed
- Update framework to 6.0.6

## [2.0.5] - 2019-08-19
### Changed
- Update framework to 6.0.5

## [2.0.4] - 2019-08-16
### Changed
- Update framework to 6.0.4

## [2.0.3] - 2019-08-16
### Changed
- Update framework to 6.0.3

## [2.0.2] - 2019-08-15
### Changed
- Update framework to 6.0.2

## [2.0.1] - 2019-08-15
### Changed
- Update framework to 6.0.1

## [2.0.0] - 2019-08-15

### Added
- Event Catchup is now Observable using the JEE event system
- Observers for the shutter then catchup process
- Add Shuttering implementation to PublisherTimerBean
- Added an Observer for PublishedEvent Rebuild
- CommandHandlerQueueChecker to check the command handler queue is empty when shuttering
- Added implementation for updating StreamStatus table with component column
- Add Published Event Source
- Add event linked tracking for processed events
- Subscription prioritisation  
- Catchup now callable from JMX bean
- Support to for linked events after stream transformation
- Java API to retrieve all stream IDs (active and inactive ones)
- truncate() method to the EventSource interface
- populate() method to the EventSource interface
- Added component to the event buffer to allow it to handle both the event listener and indexer
- Added component column to processed_event table to allow Event Listener and Indexer to create unique primary keys   

### Changed
- Catchup and rebuild moved to a new 'event-store-management' module  
- Catchup and rebuild now use the new System Command jmx architecture
- Shuttering executors no longer register themselves but are discovered at system startup and registered
- Implement usage of new system database
- Pre-publish and publish timer beans run for a given timer max runtime. The max time can be set with JNDI values "pre.publish.timer.max.runtime.milliseconds" and "event.dequer.timer.max.runtime.milliseconds".
- Renamed subscription-repository to event-tracking
- Add event-buffer-core and event-subscription-registry pom entries
- Moved shuttering from the publishing timer bean into the command api in framework
- Events for catchup and rebuild moved in from framework-api
- Merged event-publisher modules into one
- Event-Buffer functionality with changes to StreamBuffer & StreamStatus tables to support EventIndexer
- PrePublishBean now limits the maximum number of events published per run of the timer bean.
- No longer passing around event store data source JNDI name. Using default name instead
- Simplify datasource usage and setup
- Moved Subscription domain, parsing classes and builders to Framework
- Create concurrent catchup process, replays events on different streams concurrently
- Catchup now returns events from published_event rather than from event_log
- Replaced BackwardsCompatibleJsonSchemaValidator with DummyJsonSchemaValidator in Integration Tests
- Catchup, Rebuild and Index no longer call shutter/unshutter when running
- Improved utility classes for getting Postgres DataSources
- Renamed TestEventInserter to EventStoreDataAccess as an improved test class
- Added a findEventsByStreamId() method to EventStoreDataAccess
- Moved system commands to framework jmx-api
- Updated framework-api to 4.0.1
- Updated framework to 6.0.0
- Updated common-bom to 2.4.0
- Updated utilities to 1.20.1
- Updated test-utils to 1.24.3

## [1.1.3] - 2019-02-04
### Changed
- Update utilities to 1.16.4
- Update test-utils to 1.22.0
- Update framework to 5.1.1
- Update framework-domain to 1.1.1
- Update common-bom to 1.29.0
- Update framework-api to 3.2.0

## [1.1.2] - 2019-01-22
### Added
- JNDI configuration variable to enable/disable Event Catchup

## [1.1.1] - 2019-01-15
### Added
- Event Catchup on startup, where all unknown events are retrieved from the EventSource and played
- An event-number to each event to allow for event catchup
- Added a new TimerBean 'PrePublishBean'
- Added a new auto incrementing column event_number to event_log table
- Subscription event interceptor to update the current event number in the subscriptions table
- New util module
- Subscription repository
- Subscription liquibase for subscription table
- Better logging for event catchup 
- A new transaction is started for each event during event catchup
- Event catchup only runs with event listener components
- Indexes to 'name' and 'date_created' columns in the event_log table
- New util module
- Better logging for event catchup 
- An event-number to each event to allow for event catchup
- Added a new TimerBean 'PrePublishBean'
- Added a new auto incrementing column event_number to event_log table
- Subscription liquibase for subscription table
- Subscription repository
- Subscription event interceptor to update the current event number in the subscriptions table

### Changed
- Updated publish process to add events into a pre_publish_queue table
- Renamed the sequence_number column in event_stream to position_in_stream
- Renamed the sequence_id column in event_log to position_in_stream
- Tightened up transaction boundaries for event catchup so that each event is run in its own transaction
- Event catchup now delegated to a separate session bean to allow for running in RequestScope
- Current event number is initialised to zero if it does not exist on app startup
- Tightened up transaction boundaries for event catchup so that each event is run in its own transaction
- A new transaction is started for each event during event catchup
- Updated publish process to add events into a pre_publish_queue table
- Renamed the sequence_id column in event_log to position_in_stream
- Renamed the sequence_number column in event_stream to position_in_stream

## [1.0.4] - 2018-11-16
### Changed
- Updated framework-api to 3.0.1
- Updated framework to 5.0.4
- Updated framework-domain to 1.0.3

### Added
- Added a page size when reading stream of events

## [1.0.3] - 2018-11-13
### Changed
- Removed hard coded localhost from test datasource url

## [1.0.2] - 2018-11-09
### Changed
- Update framework to 5.0.3

## [1.0.1] - 2018-11-07
### Changed
- Update framework to 5.0.2

## [1.0.0] - 2018-11-07
### Changed
- Update framework to 5.0.0
- Renamed DatabaseCleaner method to match table name
- Removed the need to have event-source.yaml
- Remove requirement to have a subscription-descriptor.yaml on the classpath
- event-publisher-process to event-store-bom
- New test-utils-event-store module to hold TestEventRepository moved from framework
- Reverted names of event buffer tables to their original names: 
subscription -> stream_status, event-buffer -> stream buffer
- Moved test-utils-persistence into this project from 
[Framework](https://github.com/CJSCommonPlatform/microservice_framework)

### Added
- Extracted project from all event store related modules in Microservices Framework 5.0.0-M1: https://github.com/CJSCommonPlatform/microservice_framework


