# SudokuSolver
Very basic sudoku solver using a Swing GUI

## How to run

> Note: The following commands assume you are using a UNIX-like system. If you are using Windows, replace `./gradlew` with `./gradlew.bat`

* Compile the project.
  * `./gradlew build`
  
* Run the project.
  
  An optional `b64_grid` argument can be passed in to preload a Sudoku grid. The base64 string of a grid can be obtained by using the exporting feature of the application.
  
  * `./gradlew run [--args="b64_grid"]` 
  * `java -jar app/build/libs/sudokuSolver.jar [b64_grid]`
  
  