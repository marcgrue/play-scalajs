## Silhouette Play 2.8 Scala/ScalaJS Template
    
Play Scala/ScalaJS implementation of the [Silhouette](https://www.google.com/search?client=safari&rls=en&q=silhouette+authentication&ie=UTF-8&oe=UTF-8) authentication library. 

Unlike the other projects in this repository, this project is not published to maven but should rather be used as a template for the functionality that you need. A "subproject" has been created inside the `auth` package on the server and all routes and settings imported as sub-routes. This allows you to drop the whole authentication infrastructure into your Scala/ScalaJS Play project easily while maintaining a separation from your core domain logic. 

### Some design choices:

- Using Scalatags-based html pages instead of views - most differences to the reference implementation are found in the `views` package
- Using CSRF tokens and nounces for security
- Using message texts instead of `Message` for clarity and to ease choosing alternative i18n strategies  
- Showing example of Secret/public client pages where authentication status are passed from the server 


### Run

    sbt run

Go to <http://localhost:9000>