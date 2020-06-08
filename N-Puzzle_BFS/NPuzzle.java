// Copyright (c) 2020 https://github.com/infoseekgithub. All rights reserved.

import java.util.*;


class Pair {
    int x;
    int y;
    Pair (int x, int y) {
        this.x = x;
        this.y = y;
    }
}

enum DIR {
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    UP("UP"),
    DOWN("DOWN"),
    INIT("INIT"); //INIT is the inital state
    
    String str;
    
    DIR(String str) {
        this.str = str;
    }
    
    @Override
    public String toString() {
        return str;
    }
}


class State {
    
    List<List<Integer>> snapshot;
    State parent; // parent state
    DIR dir; //dirction to this state
        
    State(List<List<Integer>> ss, State p, DIR d) {
        snapshot = ss;
        parent = p;
        dir = d;
    }
    
    // For hash 
    @Override
    public boolean equals(Object other) {
        if (! (other instanceof State)) {
            return false;
        }

        if (other == this) {
            return true;
        }
                
        State o = (State)other;
        
        int N = snapshot.size();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (snapshot.get(i).get(j) != o.snapshot.get(i).get(j)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        String str = snapshot.toString();
	System.out.println(str);
        return str.hashCode();
    }
    
}


public class NPuzzle {

    int[][] board;
    int N; //size of board N X N
    Pair pos; //position of '0'
    Set<State> visited;       // Use a HashSet to store the already visited state
    Deque<State> queue;  // Use a ArrayDeque to store the states of the search options in BFS

    NPuzzle(int[][] bd, Pair p) {
        board = bd;
        N = board.length;
        pos = p;
        visited = new HashSet<>();
        queue = new ArrayDeque<>();
    }


    void setBoard(List<List<Integer>> snapshot) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = snapshot.get(i).get(j);
                
                if (board[i][j] == 0) {
                    pos.x = i;
                    pos.y = j;
                }
            }
        }
    }


    boolean isGoldenState() {
        int val = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                val++;
                if (i == N - 1 && j == N - 1) {
                    return true;
                }
                if (board[i][j] != val) {
                    return false;
                }
            }
        }
        
        return true;
    }


    List<List<Integer>> generateSnapshot() {
        List<List<Integer>> ret = new ArrayList<>();
        List<Integer> line;
        
        for (int i = 0; i < N; i++) {
            line = new ArrayList<Integer>();
            for (int j = 0; j < N; j++) {
                line.add(board[i][j]);
            }
            ret.add(line);
        }
        
        return ret;
    }


    void findNeighbors(State state) {
        State nextState;
        List<List<Integer>> nextSnapshot;
        
        int i = pos.x, j = pos.y;
        int tmp1 = state.snapshot.get(i).get(j);
        int tmp2;

        // LEFT
        if (j != 0) {
            nextSnapshot = new ArrayList<>();
            for (List<Integer> list : state.snapshot) {
                nextSnapshot.add(new ArrayList<Integer>(list));
            }
            tmp2 = state.snapshot.get(i).get(j - 1);
            nextSnapshot.get(i).set(j, tmp2);
            nextSnapshot.get(i).set(j - 1, tmp1);
            nextState = new State(nextSnapshot, state, DIR.LEFT);
            if (!visited.contains(nextState)) {
                queue.offer(nextState);
                visited.add(nextState);
            }
        }
        
        // RIGHT
        if (j != N - 1) {
            nextSnapshot = new ArrayList<>();
            for (List<Integer> list : state.snapshot) {
                nextSnapshot.add(new ArrayList<Integer>(list));
            }
            tmp2 = state.snapshot.get(i).get(j + 1);
            nextSnapshot.get(i).set(j, tmp2);
            nextSnapshot.get(i).set(j + 1, tmp1);
            nextState = new State(nextSnapshot, state, DIR.RIGHT);
            if (!visited.contains(nextState)) {
                queue.offer(nextState);
                visited.add(nextState);
            }
        }
        
        // UP
        if (i != 0) {
            nextSnapshot = new ArrayList<>();
            for (List<Integer> list : state.snapshot) {
                nextSnapshot.add(new ArrayList<Integer>(list));
            }
            tmp2 = state.snapshot.get(i - 1).get(j);
            nextSnapshot.get(i).set(j, tmp2);
            nextSnapshot.get(i - 1).set(j, tmp1);
            nextState = new State(nextSnapshot, state, DIR.UP);
            if (!visited.contains(nextState)) {
                queue.offer(nextState);
                visited.add(nextState);
            }
        }
        
        // DOWN
        if (i != N - 1) {
            nextSnapshot = new ArrayList<>();
            for (List<Integer> list : state.snapshot) {
                nextSnapshot.add(new ArrayList<Integer>(list));
            }
            tmp2 = state.snapshot.get(i + 1).get(j);
            nextSnapshot.get(i).set(j, tmp2);
            nextSnapshot.get(i + 1).set(j, tmp1);
            nextState = new State(nextSnapshot, state, DIR.DOWN);
            if (!visited.contains(nextState)) {
                queue.offer(nextState);
                visited.add(nextState);
            }
        }
    }
    
    

    // Use BFS to search
    boolean solveBFS() { //Return true if puzzle can be solved; false if not

        if (isGoldenState() ) {
            System.out.println("Init state is already Golden State.");
            System.out.println("--------------------\n");
            return true;
        }
        
        State state = new State(generateSnapshot(), null, DIR.INIT);
        visited.add(state);
        queue.offer(state);

        //Start BFS
        while (!queue.isEmpty()) {
            state = queue.poll();
            setBoard(state.snapshot); 
            if (isGoldenState()) {
                printPath(state);
                System.out.println("--------------------\n");
                return true;
            }

            findNeighbors(state);
        }
        
        // When queue is empty, and no GoldenState is found, we should return false,
        // meaning the puzzle is not solvable
        System.out.println("Problem is not solvable.");
        System.out.println("--------------------\n");
        return false;
    }


    void printPath(State state) {
        
        LinkedList<DIR> path = new LinkedList<>();
        while (state.dir != DIR.INIT) {
            path.push(state.dir);
            state = state.parent;
        }
        
        System.out.println("Solvable. Path to Golden State is:");
        while (!path.isEmpty()) {
            System.out.println(path.pop());
        }
    }
    

    public static void main(String[] args) {
        
        int[][] board1 = { {1, 2, 3}, 
                           {4, 5, 6},
                           {7, 0, 8}
                         };  
        NPuzzle puzzle1 = new NPuzzle(board1, new Pair(2, 1));
        puzzle1.solveBFS();

        int[][] board2 = { {1, 8, 2}, 
                           {0, 4, 3},
                           {7, 6, 5}
                         };
        NPuzzle puzzle2 = new NPuzzle(board2, new Pair(1, 0));
        puzzle2.solveBFS();
         
        int[][] board3 = { {8, 1, 2}, 
                           {0, 4, 3},
                           {7, 6, 5}
                         };
        NPuzzle puzzle3 = new NPuzzle(board3, new Pair(1, 0));
        puzzle3.solveBFS();

    }

}
