# LieToMe
SNLP Mini Project Universität Paderborn : Fact Checker

(Group: SAM) Samrat Dutta \[6855850\], Atharva Pandey \[6854309\] \[GERBIL: LieToMe\]

Our objective was to create a fact checking algorithm, that rates a given sentence as true or false, based on the data it finds over the internet.

We achieved \~80% success rate while using the SNLP Train 2018 and SNLP Test 2018 files. With an average run time of around 30 seconds for 1400+ lines (fast internet access required).

External Libraries used : Apache OpenNLP (1.9.0), JSoup(1.11.3)




=======================================================================================



*IMPORTANT INFORMATION FOR SETTING UP*:

config.xml in the root folder is important. It contains information about file paths and number of thread settings.

> File Paths : By default all the required files (including the input files) are stored in a folder called "res" in the root directory.
>
> If the input files need to be stored elsewhere, please change the corresponding config value in the 'config.xml'.
>
> The 'result.ttl' file can be found in the project root folder as well.

External libraries used : JSoup and Apache-OpenNLP.\
The jar files required are present in the 'lib' folder. The 'lib' folder needs to be included in the build path in order to produce a successful build.

The configuration also contains a 'debug' property. Please set it to 0 (default). \[When debugging is turned on, the program uses a different input file. (All paths in config.) And logs relevant data for debugging on the 'System.out'\]

**Architecture :**

**Features :**

\- Our main goal while building this prototype, other than correctly predicting the truth values of the statements, was to make a program that is ***fast***. So we traded a bit of accuracy with speed. We believe that our code is thread safe and we use multiple threads to process the slowest chunk of the code which is information retrieval form the internet.\
\[We noticed that 10-15 threads provides the best results. For some reason, increasing the number of threads might result in some of the threads suffering timeout issues. (This number can be modified through the config file.)\]

\- Our program is highly configurable using the config file. The location of input/output files can be changed at will. And the number of threads can be adjusted as well through the config file.

\- The config also contains a debug option, which lets us debug the program with a smaller dataset to get to the root of the problem.

**Approach** :

We use Apache-OpenNLP to find person names and we use the same library to tag parts of speech of the given statement. The algorithm needs to be trained before use. This happens using the model training files (provided by apache) present in the 'res' folder.

Then we determine the subject of the given statement. An apostrophe (') is a pretty good hint, usually the sentence is about the entity with the apostrophe at the end. Otherwise, we use the name of the person as the subject or consecutive capital letter proper nouns before encountering the verb.

We also determine the property that we need to map (e.g birth place, spouse), and the value we need to map the property to (the other group of noun propers).

After tagging is complete, using multiple threads, we first check if data about the subject is present in the in memory cache we maintain. If it is present, we use it, otherwise we search the data from wikipedia. In case we encounter multiple pages, we choose the first page.

Currently we are limiting our search to the infobox (the first paragraph if infobox is absent). Accuracy takes a small hit. But we can easily extend the program to parse through the entire page.

We maintain an xml file called 'knowledge\_base.xml' which maps various synonyms that we may encounter. Fact checking without knowledge about the domain, and without the knowledge of some synonyms to be matched yielded poor results (\~ 60% correctness), hence we chose to map the most frequently used synonyms in the dataset that we were given

The idea was to update these confidence values for the synonyms using the program, based on the quality of predictions(estimated vs true values). We did not get a chance to implement this, partially due to the lack of time and due to the unavailability of data marked with truth values (The training data provided was not large enough).

We extract sentences from the wikipedia data extracted based on the values we want to map. Then we assign a confidence score, based on if the property to be matched is also present in the sentence.

If nothing is found, we fetch the wiki info about the value and try to match the subject with the information extracted.

Currently we could not figure out a way to consistently rate sentences with percentage confidence values. So, we degenerate our rating system into a boolean (-1, +1) result. \[If our internal rating is \>= 0, we rate the statement 1.0, otherwise we rate it -1.0\].

**Non - Working Examples :**

1. 3451014 Finland is Paavo Lipponen\'s role. \[Estimated : True\]

2. 3417089 New Zealand is Keith Holyoake\'s role.\
\[Estimated : True\]

3. 3410956 Rihanna\'s spouse is Chris Brown (American entertainer). \[Estimated : True\]

4. 3322808	Isaac Bashevis Singer\'s award is Nobel Prize in Physiology or Medicine.\
\[Estimated : True\]

5. 3273588	Steve Blake's team is Washington Wizards.\
\[Estimated : True\]\
\
\
\
**Working Examples :**

1\. 3821119 Serena Williams\' birth place is Saginaw, Michigan.

2\. 3438211 Skylark DuQuesne\'s author is E. E. Smith.

3\. 3414184 Alki David is Anna Torv\'s better half.

4\. 3682686 Edward Scissorhands stars Johnny Depp.

5\. Abbas II of Egypt\'s birth place is Geneva.

\
\
\
\
\

**GROUP NAME : SAM**

GERBIL TEST : LieToMe

*MEMBERS :*

Samrat Dutta \[6855850\] (dutta.sam.d\@gmail.com)

Atharva Pandey \[6854309\] (razerblast\@gmail.com)

Link to GIT source code :\
https://github.com/duttasamd/LieToMe
