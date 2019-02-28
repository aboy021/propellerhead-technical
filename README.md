# propellerhead-technical

Web application built for the Propellerhead Techinical Test

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

I developed this using Java 11.

Copy `dev-config.example.edn` to `dev-config.edn`. 
This file contains config overrides - secrets that generally 
should not go in source control.

If you want to download the Marvel character data you'll need your
own API key which you should add to your `dev-config.edn`

- [Marvel Developer Portal][2]

[1]: https://github.com/technomancy/leiningen
[2]: https://developer.marvel.com/docs#!/public/getCreatorCollection_get_0

## Running

To start a web server for the application, run:

    lein run 

## Code Highlights

### Marvel Character import

`src/clj/propellerhead_technical/db_import/` contains one off code
for retrieving characters from the Marvel, transforming the data, 
and inserting it into the local database. This code is expected 
to be run from a REPL on a one off basis so lacks error handling
and logging.

## Nice to Haves

Some things that would be nice to have that I didn't implement.

### Status Normalised

I've stored status as a string, but I would prefer the DB to be better
normalised and to store a status_id and have a status table with 
status_id and description.

### Full Text Indexing

Use Lucene to index all the character descriptions. 
Would have allowed for better search, though would have to be maintained.
[Clucy](https://github.com/weavejester/clucy) is the library 
I would have used.

### AJAX

Making the pages a little bit more responsive buy using ajax queries
from client side javascript would have been good. A REST api with a 
swagger front end, combined with clojure spec definitions for the 
entities involved would have been nice.

### SPA

A Single Page Application using React, Re-Frame, and ClojureScript 
would have been pretty great. I decided against it due to the 
complexity and time.

## General Notes

generated using Luminus version "3.10.40"

## License

Copyright © 2019 Arthur Boyer
