# propellerhead-technical

Simple Web application built for the Propellerhead Techinical Test.

<!-- MarkdownTOC  autolink="true" autoanchor="true" -->

- [Introduction](#introduction)
- [Screenshots](#screenshots)
    - [Main Screen](#main-screen)
    - [Detail Screen](#detail-screen)
- [Running the app](#running-the-app)
- [Some Code Highlights](#some-code-highlights)
- [Nice to Haves](#nice-to-haves)
    - [Security](#security)
    - [Database Not in Git](#database-not-in-git)
    - [Status Normalised](#status-normalised)
    - [Full Text Indexing](#full-text-indexing)
    - [AJAX](#ajax)
    - [SPA](#spa)
- [License](#license)

<!-- /MarkdownTOC -->

<a id="introduction"></a>
## Introduction

Since this is something I'm building on my own time I've opted to use:

- [Clojure](https://clojure.org/)
    - My favourite programming language
- [Cursive](https://cursive-ide.com/)
    - An IntelliJ plugin for Clojure development (Made in NZ)
- [Luminus](http://www.luminusweb.net/)
    - A Clojure web framework I've been meaning to try
- [H2 Database Engine](http://www.h2database.com/html/main.html)
    - A Java embedded database that I haven't used before
- [Java 11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
    - The current version
- The [Marvel Developer API](https://developer.marvel.com/docs)
    - Because comic book characters are more fun than synthetic data

<a id="screenshots"></a>
## Screenshots

<a id="main-screen"></a>
### Main Screen

<a id="detail-screen"></a>
### Detail Screen

<a id="running-the-app"></a>
## Running the app

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.0 or above installed.

I developed this using Java 11.

Copy `dev-config.example.edn` to `dev-config.edn`. 
This file contains config overrides - secrets that generally 
should not go in source control.

If you want to download the Marvel character data you'll need your
own API key from the [Marvel Developer Portal](https://developer.marvel.com/). You should add this to your `dev-config.edn`

To start a web server for the application, run:

    lein run

The open a web browser to [http://localhost:3000/](http://localhost:3000/)


<a id="some-code-highlights"></a>
## Some Code Highlights

- `src/clj/propellerhead_technical/db_import/`
    - One off Marvel character import code. Run from a REPL.
- `resources/sql/queries.sql`
    - [HugSQL](https://www.hugsql.org/) formatted sql queries .
- `src/clj/propellerhead_technical/routes`
    - Page routes. Effectively controllers in an MVC sense.
- `resources/html/`
    - Html templates. Rendered using [Selmer](https://github.com/yogthos/Selmer).


<a id="nice-to-haves"></a>
## Nice to Haves

Some things that would be nice to have that I didn't implement.

<a id="security"></a>
### Security

As it is now, anyone can do anything to any record. There's no notion of users or logging in. The site isn't encrypted. There's very little validation of user input.


<a id="database-not-in-git"></a>
### Database Not in Git

I've stored the dev database in git. It's small, so it's 
not that big of a deal, but it's not best practice. 

<a id="status-normalised"></a>
### Status Normalised

I've stored status as a string, but I would prefer the DB to be better
normalised and to store a status_id and have a status table with 
status_id and description.

<a id="full-text-indexing"></a>
### Full Text Indexing

Use Lucene to index all the character descriptions. 
Would have allowed for better search, though would have to be maintained.
[Clucy](https://github.com/weavejester/clucy) is the library 
I would have used.

<a id="ajax"></a>
### AJAX

Making the pages a little bit more responsive buy using ajax queries
from client side javascript would have been good. A REST api with a 
swagger front end, combined with clojure spec definitions for the 
entities involved would have been nice.

<a id="spa"></a>
### SPA

A Single Page Application using React, Re-Frame, and ClojureScript 
would have been pretty great. I decided against it due to the 
complexity and time.

<a id="license"></a>
## License

Copyright © 2019 Arthur Boyer
Data provided by Marvel. © 2014 Marvel
