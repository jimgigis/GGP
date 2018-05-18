
# The 3-puzzle is a very simple sliding tile puzzle. 
# The game board is a 2x2 square with numbered tiles in all but one of the cells. 
# The state of the game is modified by sliding numbered tiles into the empty space from adjacent cells, 
# thus moving the empty space to a new location. 
# There are four possible moves - moving the empty space up, down, left, or right. Obviously, not all moves are possible in all states. 
# The ultimate object of the game is to place the tiles in order and position the empty square in the lower right cell. 
# The game terminates after 6 moves. The goal state is worth 100 points; all other states are worth 0 points.  


# base proposition
role(player)

base (cell X,Y,Z):-
	index(X) &
	index(Y) &
	tile(Z)

# initial state 
init(cell(1,1,b))
init(cell(1,2,3))
init(cell(2,1,2))
init(cell(2,2,1))
init(turn(1))

#    Data     
turnsuccesor(1,2)
turnsuccesor(2,3)
turnsuccesor(3,4)
turnsuccesor(4,5)
turnsuccesor(5,6)
turnsuccesor(6,7)

index(1)
index(2)

tile(1)
tile(2)
tile(3)
tile(b)

input(player,up)
input(player,down)
input(player,left)
input(player,right)


# legal moves 
legal(player, up) :-
	true(cell(2, X , b))
legal(player, down) :-
	true(cell(1, X , b))
legal(player, left) :-
	true(cell(X, 2 , b))
legal(player, right) :-
	true(cell(X, 1 , b))

# update rules 
next(cell(1, X , b)) :-
	does(player,up) &
	true(cell(2, X ,b))

next(cell(2, X ,b)) :-
	does(player,down) &
	true(cell(1, X, b))

next(cell(X, 1, b)) :-
	does(player,left) &
	true(cell(X, 2, b))

next(cell(X, 2, b)) :-
	does(player,right) &
	true(cell(X, 1, b))

next(turn X) :-
	true(turn(Y)) &
	turnsuccesor(Y,X)	

# goal & terminal state 
goal (player, 100) :-
	true(cell(1,1,1)) &
	true(cell(1,2,2)) &
	true(cell(2,1,3))

goal(player, 0) :-
	~(true(cell(1,1,1)))

goal(player, 0 ) :-
	~(true(cell(1,2,2)))

goal(player, 0) :-
	~(true(cell(2,1,3)))

terminal :-
	true(turn(7))

terminal :-
	true(goal(player, 100))

