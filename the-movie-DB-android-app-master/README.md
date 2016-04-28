# Actors And Actresses FilmographyApp
![alt text](http://i.imgur.com/Ms9GWSO.png "Main Activity Snapshot")


### Main Activity:

###### Portrait view

![alt text](http://i.imgur.com/Fp2qEYH.jpg?1 "Main Activity Snapshot")



The main activity is composed by two fields name and last name which the user must fill before searching if any of the fields is invalid the app asks the user for a new input.

When the input for both fields is valid on click of search button will take you to the second activity which display all the filmography for the searched actor or actress.

This app consumes REST services from theMovieDb.org so for example if we want to search for Morgan Freeman we first use the following `GET` method:

`http://api.themoviedb.org/3/search/person?api_key=######&query=morgan+freeman`

* Replace #### with your personal api key.


This will give us the ID for Morgan Freeman in JSON format: `id: 192`


Now we can use his ID to display all the filmography for Morgan Freeman with a `GET` method such as this:
`https://api.themoviedb.org/3/person/192?api_key=########&append_to_response=credits`

notice: `id: 192`

We will use the past `GET` request in the Actor Filmography Activity.

### Actors Activity:

###### Portrait view

![alt text](http://i.imgur.com/v0N9Cjw.jpg?1)

This activities display actors/actresses related to the search query. On click of item it will show you full actor filmography.



### Films Activity:

###### Portrait view

![alt text](http://i.imgur.com/wEUY9f5.jpg?1 )

This activity display the full filmography for the actor including not released films which have a future release date. The filmography is sorted by the films release date in ascending order. 

Once we retrieve the filmography for our desired actor/actress we parse the JSON response and display images for the actor/actress and it's films, a placeholder is used in case no poster is found for a movie.


### REST services from theMovieDB API

This android app uses the API from: https://www.themoviedb.org/ 

### Libraries
The app also uses third party libraries such as:
* **Butterknife** for injecting views with less boilerplate code.
* **Picasso** for easily fetching images from URL's.
* **OkHTTP** for easy HTTP connectivity.
