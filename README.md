CPA
===================

CPA is a tool for generating data streams.  A data stream is a sequence of measurements from a sensor or other source, over time (for example te$

It is meant to be used as a tool for testing the performance of systems for gathering and processing large amounts of data from different source$

Current Features Include:

 - Simple to use, command-line user interface.
 - Data Generation using different distributions (Poisson, Exponential, Geometric, Pareto, Gaussian, Uniform/Random and Constant).
  - Ability to specify starting date of data collection, periodicity (amount of time between subsequent measurements), prefix of URI of generate$
  - Uses the [MUO ontology](http://idi.fundacionctic.org/muo/) and the [UCUM vocabulary](http://idi.fundacionctic.org/muo/ucum-instances.owl) fo$
  - Ability to specify stream metadata in the same information model (e.g. description and geographical location)
  - Uses [Turtle](http://www.w3.org/TeamSubmission/turtle/) (.ttl) notation to export the generated files.
 - Data Playback support with built-in validation of .ttl using [Apache Jena](https://www.google.se/url?sa=t&rct=j&q=&esrc=s&source=web&cd=1&cad$
  - Customizable speed of playback (real-time, accelerated, decelerated)
  - Customizable mode of data transmission (JSON/UDP sockets)

-------------

Installation and Execution
-------------

To install CPA, clone this project and do a maven install.

```
git clone xxx.git
mvn install
```

The executable can be found in the "target" folder and can be ran from the command line:

`cd target; java -jar cpa-x-jar-with-dependencies.jar`

Command Line Client Documentation
-------------

The supported commands can be accessed by pressing Tab on the command prompt. The tab key can also be used for autocompletion. Currently, the fo$

 - generate: Generates a new data stream
 - playback: Plays back an existing dataset. This command has to be provided with an argument, i.e. the name of the dataset file to be used for $
 - help: Provides a help menu
 - clear: Clears the screen
 - exit: Exit the CPA client

The rest of the document describes the data generation and the data playback functions.

###Data Generation

The user can generate a new dataset by typing `generate` in the CPA command prompt. 

> cpa> `generate `

Subsequently the user can parameterize the data stream by typing in a number of properties. First the name of the data stream.

> Stream Description (e.g. "This stream monitors temperature in Manhattan, New York") >`Temperature Stream in Rabalder Meeting Room`

Then, the number of measurements to be generated:

> Number of measurements [positive integer] >`10000`

Subsequently the starting date of the observations:

> Starting Date: To be provided in yy-MM-ddTHH:mm:ss format, e.g. 2014-12-12T11:11:11 >`2014-10-30T18:57:00`

The periodicity between the observations is defined next:

> Periodicity: Amount of time between two subsequent measurements [positive integer] >`1`

> Periodicity Unit of Measurement [1=minutes, 2=seconds and 3=milliseconds] >`1`

The type of the generated values is set next. It is recommended to use double numbers so that values of some distributions are more accurate:

> Observation Value Type: [1=integer, 2=double], double recommended >`2`

The next option sets the data stream name, which is to be used inside the information model. Please use one word and avoid using special characters.

> Datastream Name: (please use one word, e.g. "temperatureStream, carTraffic", etc.) >`temperatureRabalder`

Then the type of the distribution used to calculate the values:

> Distribution: [1=Poisson(mean lambda) 2=Exponential(rate lambda) 3=Geometric(mean 1/p) 4=Pareto(a) 5=Uniform(bounded) 6=Constant 7=Gaussian(mean, stdev)] >`5`

Depending on the choice of value distribution, different parameters are asked:

> Upper Bound: Upper bound for value generation in uniform distribution >`20`
> Lower Bound: Lower bound for value generation in uniform distribution >`5`

The output mode should always be file, console can be used for verification/debugging:

> Output Mode: [1=console 2=file] >`2`
> Filename >`temperature_stream_rabalder.ttl`

As explained in the introduction, the unit of measurement is based on the [UCUM vocabulary](http://idi.fundacionctic.org/muo/ucum-instances.owl), so the user is asked here to specify a valid term (note that the citypulse project has added a few more definitions as well). The list command provides convenient access to all units of measurements:

> Unit of measurement [paste URI or type 'list' for possible values]>`list`

> List of supported units of measurement: 

> [0]	http://purl.oclc.org/NET/muo/ucum/unit/length/meter

> [1]	http://purl.oclc.org/NET/muo/ucum/unit/time/second

> [2]	http://purl.oclc.org/NET/muo/ucum/unit/mass/gram

> [...]

> [266]	http://purl.oclc.org/NET/muo/citypulse/unit/traffic/vehicle-count

> [267]	http://purl.oclc.org/NET/muo/citypulse/unit/velocity/km-per-hour

> [268]	http://purl.oclc.org/NET/muo/citypulse/unit/time/seconds

> [269]	http://purl.oclc.org/NET/muo/citypulse/unit/events/eventData

> Pick a number of measurement that better represents your data >`28`


Next, the prefix of the URL for the entities defined in the generated ontology has to be defined. It is advisable to put the name of the host where the ontology will be deployed.


> Datastream Prefix (URL where the stream will be placed, e.g. http://info.ee.surrey.ac.uk/CCSR/CityPulse) >`http://era-gsr.ki.sw.ericsson.se/datasets/generated/rabalder`

Finally, the data stream is generated. The time spend on data stream generation depends on the number of observations set from the user before. The generated file can be found in the root folder from where the CPA CLI application was run.


###Data Playback

Data Playback allows for simulation of data transmission from an existing data stream file (such as the one generated in the Data Generation section above).
