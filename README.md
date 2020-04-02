# Go game

Game was written using Java 11, Docker and Makefile, please make sure, that you have them installed before running application

### Usage example:

Firstly, run command `make db`

For server start, run command `make server-run`

For client start, run command `make client-run`

*you can modify port and host of server and client in `Makefile` script

### Protocol:

- Authentication step commands

    from client to server
    ```$xslt
    LOGIN <username> <password>                     authorization
  
    SIGN_UP <username> <password>                   registration
  
    QUIT                                            quit
    ```
    
    from server to client
    ```$xslt
    NO_USER <username>                              failed authorization
                                                    caused by unexisted username
  
    FAILED_AUTH                                     failed authorization
                                                    caused by wrong password
  
    ALREADY_USER <username>                         failed registration
                                                    caused by already existing username
    
    SUCCESS_AUTH                                    success authorization or registration
    ```
  
- Lobby interaction step commands

    from client to server
    ```$xslt
    CREATE_LOBBY <lobbyName>                        create lobby with exact name
    
    JOIN_LOBBY <lobbyId>                            join to the lobby with exact id
  
    QUIT_LOBBY                                      quit from lobby
  
    READY                                           be ready for game
  
    START                                           start game (available when game started obly)                  
    ```
    from server to client
    ```$xslt
    FAILED_JOIN <lobbyId> <cause>                   failed join to lobby with exact id
  
    FAILED_JOIN <cause>                             failed to create lobby
  
    SUCCESS_JOIN                                    success join to lobby
  
    USER_JOINED <username>                          notification, that user with username joined lobby
  
    USER_QUIT   <username>                          notification, that user with username quit lobby
  
    USER_READY  <username>                          notification, that user with username is ready
  
    USER_NOT_READY  <username>                      notification, that user with username is not ready
  
    ALL_READY                                       sign, that all users are ready and command START is available
  
    FAILED_START                                    start was failed, because some of players are not ready
  
    GAME_STARTED                                    message, that game was started
  
    DEBUG_INFO                                      debug information from server
    ```

- Game step commands

    from client
    ```$xslt
    MOVE <x> <y>                                    put stone on intersection with coordinates (x, y)
  
    PASS                                            pass command
  
    HISTORY                                         history of game at current moment
  
    QUIT                                            quit game
    ```
  
    from server
    ```$xslt
    FAILED_MOVE <cause>                             failed move with explained cause
  
    BOARD   <board>                                 whole board on exact time
    ```



