Files: 

Node.java :- Contains all the attributes of a node class
DStar.java :- Contains main algorithm where it finds the most optimal path

Assumptions:

1) User should always enter the correct information as asked.
2) The cell numbers will range from 1 to N^2 where N = board size
3) Once the obstacle is placed it cannot be removed
4) You can add more obstacles when asked, but you cannot add obstacle on top of obstacle
5) The borders are considered as walls, and robot cannot go through the wall
6) Robot cannot go through the obstacle
7) Assumed Heuristic is Manhattan Distance

Run:

1) Compile all java files : javac *.java
2) Run the DStar file : java DStar
3) Enter the size of the board (eg 5,10)
4) Enter the start and goal cell number
5) Enter the number of obstacles you want to add initially
6) Enter the cell number of each obstacle
7) After every stage, you will be asked if you want to add any obstacle
8) Enter 0 if you dont want to add any obstacles, otherwise enter the number of obstacles
9) If there is no path to goal, it will show No path found, otherwise the code will run till till the robot reaches the goal node