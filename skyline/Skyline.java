// Copyright (c) 2020 https://github.com/infoseekgithub. All rights reserved.


import java.util.*;

class Event{
    int position; //where the event happens
    boolean hasRising; //true if it has rising event
    boolean hasSinking;
    int risingHeight;
    int sinkingHeight;
    List<Building> buildingList; //only useful for rising events, to store the building it belongs
    
    Event(int pos, boolean isRising, int height) {
        position = pos;
        buildingList = new ArrayList<Building>();
        
        if (isRising) {
            hasRising = true;
            risingHeight = height;
        } else {
            hasSinking = true;
            sinkingHeight = height;
        }
    }
    
    void update(boolean isRising, int height) {
        if (isRising) {
            hasRising = true;
            risingHeight = Math.max(risingHeight, height);
        } else {
            hasSinking = true;
            sinkingHeight = Math.max(sinkingHeight, height);
        }
    }
    
    void updateBuildingList(int sinkingPos, int height) {
        buildingList.add(new Building(sinkingPos, height));
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Event at pos ").append(position);
        if (hasRising) {
            sb.append(": Rising to ").append(risingHeight);
        } 
        if (hasSinking) {
            sb.append(": Sinking from ").append(sinkingHeight);
        }
        sb.append('\n');
        if (!buildingList.isEmpty()) {
            sb.append("Building list:\n");
            sb.append(buildingList.toString()).append('\n');
        }
        sb.append('\n');
        return sb.toString();
    }
}


class Building {
    int sinkingPos; // only sinkingPos is needed, Builing is for prioQueue to use
    int height;
    
    Building(int sPos, int h) {
        sinkingPos = sPos;
        height = h;
    }
    
    @Override
    public String toString() {
        return "(" + sinkingPos + " , " + height + ")";
    }
}


public class Skyline {
    
    static List<List<Integer>> getSkyline(int[][] buildings) {
        
        List<List<Integer>> ret = new ArrayList<>();
        
        List<Event> events = new ArrayList<>();
        
        Map<Integer, Event> map = new HashMap<>(); // mapping from event position to event in the 'events' list

        PriorityQueue<Building> prioQueue = new PriorityQueue<>( (o1, o2) -> o2.height - o1.height ); //max priority queue
              
        for (int i = 0; i < buildings.length; i++) {
     
            // Event processing
            Event ev;
            
            // on the rising side
            if (map.containsKey(buildings[i][0])) { // buildings[i][0] is the rising position
                ev = map.get(buildings[i][0]);
                ev.update(true, buildings[i][2]); //rising event
                ev.updateBuildingList(buildings[i][1], buildings[i][2]);
            } else {
                ev = new Event(buildings[i][0], true, buildings[i][2]);
                ev.updateBuildingList(buildings[i][1], buildings[i][2]);
                map.put(buildings[i][0], ev);
                events.add(ev);
            }
            
            // on the sinking side
            if (map.containsKey(buildings[i][1])) { // buildings[i][1] is the sinking position
                ev = map.get(buildings[i][1]);
                ev.update(false, buildings[i][2]); //rising event
            } else {
                ev = new Event(buildings[i][1], false, buildings[i][2]);
                map.put(buildings[i][1], ev);
                events.add(ev);
            }
        }
        
        // Now sort the ArrayList
        events.sort((o1, o2) -> o1.position - o2.position);
        System.out.println(events);
        
        // Traverse the events list and process each event
        for (int i = 0; i < events.size(); i++) {
            Event ev = events.get(i);
            
            // Remove outdated buildings since they will not span across this position
            while (!prioQueue.isEmpty() && prioQueue.peek().sinkingPos <= ev.position) {
                prioQueue.poll();
            }
            
            if (ev.hasRising && !ev.hasSinking) {
                if (prioQueue.isEmpty() || (prioQueue.peek().height < ev.risingHeight) ) {
                    List<Integer> list = new ArrayList<>();
                    list.add(ev.position);
                    list.add(ev.risingHeight);
                    ret.add(list);
                }
                
                // Update prioQueue
                for (int k = 0; k < ev.buildingList.size(); k++) {
                    prioQueue.offer(ev.buildingList.get(k));
                }
                
            } else if (!ev.hasRising && ev.hasSinking) {
                List<Integer> list = new ArrayList<>();
                list.add(ev.position);
                
                if (prioQueue.isEmpty()) {
                    list.add(0);
                    ret.add(list);
                } else if (prioQueue.peek().height < ev.sinkingHeight ) {
                    list.add(prioQueue.peek().height);
                    ret.add(list);
                }
            } else if (ev.hasRising && ev.hasSinking) {
                if (ev.risingHeight != ev.sinkingHeight) {
                    if (prioQueue.isEmpty() || (prioQueue.peek().height < Math.max(ev.risingHeight, ev.sinkingHeight)) ) {
                        List<Integer> list = new ArrayList<>();
                        list.add(ev.position);
                        
                        if (ev.sinkingHeight > ev.risingHeight) {
                            list.add(Math.max(ev.risingHeight, prioQueue.peek().height));
                        } else {
                            list.add(ev.risingHeight);
                        }
                        ret.add(list);
                    }
                }
                // Update prioQueue
                for (int k = 0; k < ev.buildingList.size(); k++) {
                    prioQueue.offer(ev.buildingList.get(k));
                }              
            }
        }

        return ret;
    }
    
    
    public static void main(String[] args) {

        // Test 1: Rising and sinking collocate
        int[][] buildings1 = new int[][] {  {1, 100, 10},
                                            {2, 3, 8},
                                            {2, 3, 12}, //equal height rising/sinking
                                            {3, 4, 12},
                                            {3, 4, 11},
                                            {5, 6, 9},
                                            {6, 7, 9},
                                            {8, 9, 10},
                                            {9, 10, 10},
                                            {11, 12, 12}, //lower sinking, higher rising
                                            {12, 13, 14},
                                            {15, 16, 8},
                                            {16, 17, 15},
                                            {18, 19, 5},
                                            {19, 20, 9},
                                            {21, 22, 10},
                                            {22, 23, 17},
                                            {24, 25, 6},
                                            {25, 26, 10},
                                            {27, 28, 9}, // higher sinking, lower rising
                                            {28, 29, 5},
                                            {30, 31, 16},
                                            {31, 32, 8},
                                            {33, 34, 16},
                                            {34, 35, 12},
                                            {36, 37, 10},
                                            {37, 38, 8},
                                            {39, 40, 12},
                                            {40, 41, 10}
        };
        List<List<Integer>> skyline1 = getSkyline(buildings1);
        System.out.println(skyline1);                                            
        
        // Test 2: Spans intersact
        int[][] buildings2 = new int[][] {  {2, 9, 10},
                                            {3, 7, 15}, 
                                            {5, 12, 12}, 
                                            {15, 20, 10}, 
                                            {19, 24, 8} 
        };
                                          
        List<List<Integer>> skyline2 = getSkyline(buildings2);
        System.out.println(skyline2);
    }
}
