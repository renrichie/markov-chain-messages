# markov-chain-messages

![Picture](https://puu.sh/wkbIR/8443e9fc10.png "Example of the program in action") 

This program uses markov chaining, which is a probabilistic model that can be applied to many different cases, in order to analyze the data input and then randomly generate messages. Markov chaining can be used for and is used in many applications, such as predictive text and modeling of systems such as weather forecasts. My implementation of markov chaining utilizes a hashmap that essentially functions as a histogram, containing all next possible states from the current model, which can also be likened to a directed graph structure. The next possible state is then obtained by performing a weighted selection using a probability mass function, which normalizes the number of occurences of each state in the histogram so that the sum of all the probabilities totals one (1).

In order to avoid sentences being generated with words entirely capitalized, I opted to retain information on each word to indicate if it was previously capitalized in the data, and to only capitalize the start of every sentence. This, of course, means that proper nouns are not properly capitalized in the middle of a sentence. The main option I am considering is using Named Entity Recognition in order to capitalize the words, but most of the ones I have seen require use of machine learning, which is currently beyond the scope of this project.

I chose to use the Apache Commons and Twitter4J libraries because I understand that I currently lack the technical capability of properly implementing the functionality I sought in a bug-free, efficient, and optimized manner.

Inspired by https://www.reddit.com/r/SubredditSimulator/

REQUIREMENTS
==========

Libraries
----------
- Apache Commons : http://commons.apache.org/proper/commons-math/index.html
- Twitter4J      : http://twitter4j.org/en/index.html#download

Twitter API Keys and Access Tokens (Place them in a textfile named 'keys' in the directory 'assets')
- Consumer Key
- Consumer Secret
- OAuth Access Token
- OAuth Access Token Secret

COMPLETED
==========

Iteration 1
----------
- Finish the model
- Test the model

Iteration 2
----------
- Create a GUI
- Make the data persistent

Iteration 3
----------
- Refactoring and polishing
- Implement usage of Twitter API to allow for scraping of user profile as input
- ~~Make GUI resemble the format of a Twitter post~~ 

FUTURE PLANS
==========
- Add Named Entity Recognition for properly capitalizing proper nouns
- Ensure message length to be under 150 characters in order to follow the format of a Twitter post
- Optimization and additional refactoring
