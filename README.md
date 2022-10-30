# Sport management system


Suppose you need to implement a system of organization for sports competitions
in one of the cyclic sports: running, cross-country skiing, swimming, cycling, orienteering, etc.

In the simplest case, the competition has a name, a date, and implies the passage of 
each athlete to pass a single distance.

All athletes perform in different groups, depending on gender and age. The list of groups is determined
competition rules and is published in advance.

Each group has its own distance, and some groups may have the same distance.

Athletes compete for different groups, each group submits application lists in which
indicates the athletes name, surname, year of birth, sports category, desired group.
The entry lists also include medical examination and accident insurance for each athlete.

A start list is prepared for each group on the basis of all entry lists.
The protocol is formed as a result of the draw. Each athlete receives an individual bib number and start time.
The start can be either a common start (at the same time) or a split start. In the simplest case, the draw arranges all athletes in a group in random order.
However, there may be more complicated types of tosses, e.g. when it is necessary to consider the category, to consider the race within the group,
to take into account the simultaneous start of different groups.

After the competition, a score sheet is generated, as well as a protocol with the intermediate results of the course.
A course may consist of several checkpoints, each of which is timed.
The results are either recorded manually or by one or more electronic punching systems.
The result is accordingly fed into the system either by manual entry or by the receiving of data from the electronic punching system.
Usually, this is either a list of the form <number> - <time> for a given checkpoint, or
or a list of the form <checkpoint> - <time> for a given number (competitor).

In start and finish protocols, it is necessary to indicate the number, name, surname, year of birth, sports category, team for each competitor.
In the start protocol, the start time shall be indicated in addition.
In the results log, the final place, result (time spent on the distance), the lag from the leader and (optionally) the fulfilled sports category are indicated.
Exercised sports category is calculated by a certain formula which depends on the sport, group and the specific competition rules.
In addition to the results protocol for each group, a results protocol for the teams is formed.
According to a certain formula, which depends on the regulations of specific competitions, the result of each athlete in his group
gives a certain number of points, which together give the result of the team.

An example of the entry list (CSV):

```csv.
Vyborg SDShSOR ¹10,,,,,,,
Ivanov,Ivan,2002,KMS,M21,,
Petrov,Petr,1978,I,M40,,  
Pupkin,Vasiliy,2011,3rd,M10,,
```

An example of a start list for a group (CSV):

```csv
М10,,,,,,
241,Ivanov,Ivan,2011,3ю,12:01:00,
242,Ivanov,Ivan,2011,3ю,12:02:00
243,Ivanov,Ivan,2012,,12:03:00
```

An example of participant's log of the course (CSV):

```csv
243,,
1km,12:06:15
2km,12:10:36
Finish,12:14:51
```

An example of a checkpoint protocol (CSV):

```csv
1km,,
241,12:04:17
242,12:05:11
243,12:06:15
```

An example of a results protocol (CSV):

```csv
М10,,,,,,,
1,242,Petrov,Petr,2011,3ю,00:12:51,
2,243,Petrov,Petr,2012,,00:12:57,
3,241,Petrov,Petr,2011,3ю,00:13:15
```
