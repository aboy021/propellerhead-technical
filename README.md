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

## General Notes

generated using Luminus version "3.10.40"

## License

Copyright © 2019 Arthur Boyer
