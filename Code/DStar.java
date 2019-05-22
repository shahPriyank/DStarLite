import javafx.util.Pair;

import java.util.*;

public class DStar {
    static int BOARD_SIZE;
    static int MAX_VALUE = 10000;
    private static int MAX_MAX_VALUE = 1000000;
    private static int KM = 0;
    private static int[] DX = {0,-1,-1,-1,0,1,1,1};
    private static int[] DY = {-1,-1,0,1,1,1,0,-1};
    private static String[][] BOARD;
    private static PriorityQueue<Node> PRIORITY_QUEUE =
            new PriorityQueue<>(new NodeComparator());

    static class NodeComparator implements Comparator<Node> {
        public int compare(Node n1, Node n2) {
            double value1 = Math.min(n1.getgValue(),n1.getRhsValue());
            double value2 = Math.min(n2.getgValue(),n2.getRhsValue());
            if (value1 + n1.getHeuristic() + KM >
                    value2 + n2.getHeuristic() + KM) {
                return 1;
            } else if (value1 + n1.getHeuristic() + KM <
                    value2 + n2.getHeuristic() + KM) {
                return -1;
            } else {
                if (value1 > value2)
                    return 1;
                else if (value1 < value2)
                    return -1;
                else
                    return 0;
            }
        }
    }

    public static void main(String[] args) {
        initializeBoard();
        Pair<Node, Node> startGoal = findStartGoalNodes();
        dStarLitePathFinding(startGoal.getKey(), startGoal.getValue());
    }

    private static void dStarLitePathFinding(Node startNode, Node goalNode) {
        if (!computeShortestPath(startNode, goalNode)) {
            System.out.println("No path found");
            return;
        }
        if (noPathFoundCondition(startNode.getgValue())) {
            return;
        }
        BFS(startNode, goalNode);
        checkIfGoalIsReached(startNode, goalNode);
    }

    private static void BFS(Node startNode, Node goalNode) {
        NodeManager nm = NodeManager.getInstance();
        Queue<Node> queue = new LinkedList<>();
        List<Node> visited = new ArrayList<>();
        queue.add(goalNode);
        visited.add(goalNode);
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            double rhsVal = curr.getRhsValue();
            int x,y;
            double val;
            for (int i=0;i<DX.length;i++) {
                x = curr.getPosX() + DX[i];
                y = curr.getPosY() + DY[i];
                if (isValid(x,y)) {
                    if (BOARD[x][y].equals("O"))
                        continue;

                    if (curr.getPosX() != x && curr.getPosY() != y) {
                        val = 1.4;
                    } else
                        val = 1.0;

                    Node child;
                    if (nm.containsNode(x + " " + y)) {
                        child = nm.getNode(x + " " + y);
                    } else {
                        child = new Node(x,y);
                        child.setRhsValue(rhsVal + val);
                        setValuesForNode(child, startNode);
                        nm.addNode(child);
                    }
                    if (!visited.contains(child)) {
                        visited.add(child);
                        queue.add(child);
                    }

                }
            }
        }
    }

    private static boolean noPathFoundCondition(Double gVal) {
        if (gVal >= MAX_MAX_VALUE) {
            printBoard();
            System.out.println("No path found");
            return true;
        }
        return false;
    }

    private static void checkIfGoalIsReached(Node startNode, Node goalNode) {
        Node lastNode = startNode;
        Node prevNode = startNode;
        int count = 1;
        while (!startNode.equals(goalNode)) {

            Pair<Node, Double> minValues = findMinNodeAndMinCost(startNode);
            Node minNode = minValues.getKey();
            double minCost = minValues.getValue();

            if (noPathFoundCondition(minCost))
                break;

            if (minNode.getKey() < calculateKey(minNode) &&
                    calculateKey(minNode) >= MAX_VALUE) {
                printBoard();
                System.out.println("No path found");
                break;
            }

            if(!BOARD[startNode.getPosX()][startNode.getPosY()].equals("S")) {
                BOARD[startNode.getPosX()][startNode.getPosY()] =
                        Integer.toString(startNode.getPosX() * BOARD_SIZE +
                                startNode.getPosY() + 1);
            }

            if (prevNode.equals(minNode)) {
                count++;
                if (count == 7) {
                    printBoard();
                    System.out.println("No path found");
                    break;
                }
            } else {
                count = 1;
            }

            prevNode = startNode;
            startNode = minNode;

            if (pathFoundCondition(startNode, goalNode)) {
                break;
            }

            BOARD[startNode.getPosX()][startNode.getPosY()] = "C";

            if (noPathFoundCondition(startNode.getgValue())){
                break;
            }

            printBoard();

            askForAddingObstacles(startNode, goalNode);
            KM = KM + calculateHeuristic(lastNode, startNode);
            lastNode = startNode;
            if (!computeShortestPath(startNode, goalNode)){
                System.out.println("No path found");
                break;
            }
        }
    }

    private static boolean pathFoundCondition(Node startNode, Node goalNode) {
        if(startNode.equals(goalNode)) {
            printBoard();
            System.out.println("Reached Goal");
            return true;
        }
        return false;
    }

    private static Pair<Node, Double> findMinNodeAndMinCost(Node startNode) {
        NodeManager nm = NodeManager.getInstance();
        Node minNode = new Node();
        double minCost = Integer.MAX_VALUE;

        for (int i = 0; i< DX.length; i++) {
            int x = startNode.getPosX() + DX[i];
            int y = startNode.getPosY() + DY[i];
            String key = x + " " + y;

            if (isValid(x,y)) {
                if (!nm.containsNode(key)) {
                    Node successorNode = new Node(x, y);
                    setValuesForNode(successorNode, startNode);
                    nm.addNode(successorNode);
                }
                double currentCost = calculateCost(startNode,
                        nm.getNode(key)) + nm.getNode(key).getgValue();
                if (minCost > currentCost) {
                    minCost = currentCost;
                    minNode = nm.getNode(key);
                }
            }
        }

        minCost = groundValue(minCost);

        return new Pair<Node, Double>(minNode, minCost) {};
    }

    private static double groundValue(double val) {
        if (val>=MAX_MAX_VALUE)
            return MAX_MAX_VALUE;
        else if (val>=MAX_VALUE)
            return MAX_VALUE;
        return val;
    }

    private static void askForAddingObstacles(Node startNode, Node goalNode) {
        NodeManager nm = NodeManager.getInstance();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to enter any obstacles?");
        System.out.println("If Yes enter the number, else enter 0");
        int obstacleCount = scanner.nextInt();

        for (int i=0;i<obstacleCount;i++) {
            System.out.println("Enter the cell number for the Obstacles");
            int ob = scanner.nextInt();
            int x = (ob-1)/BOARD_SIZE;
            int y = (ob-1)%BOARD_SIZE;
            BOARD[x][y] = "O";
            String key = x+" "+y;
            callUpdateVertex(startNode, goalNode, nm.getNode(key),true);
        }

    }

    private static void setValuesForNode(Node currentNode, Node startNode) {
        currentNode.setHeuristic(calculateHeuristic(currentNode, startNode));
        currentNode.setKey(calculateKey(currentNode));
    }

    private static double calculateKey(Node currentNode) {
        double val = Math.min(currentNode.getgValue(),
                currentNode.getRhsValue());
        val = groundValue(val);

        return val + currentNode.getHeuristic() + KM;
    }

    private static boolean computeShortestPath(Node startNode, Node goalNode) {
        if (PRIORITY_QUEUE.isEmpty())
            return false;

        while (PRIORITY_QUEUE.peek().getKey() < calculateKey(startNode) ||
                (startNode.getgValue() != startNode.getRhsValue())){
            double kOld = PRIORITY_QUEUE.peek().getKey();
            Node top = PRIORITY_QUEUE.poll();

            assert top != null;
            if (kOld < calculateKey(top)) {
                setValuesForNode(top, startNode);
                PRIORITY_QUEUE.add(top);
            } else if (top.getgValue() > top.getRhsValue()) {
                top.setgValue(top.getRhsValue());
                callUpdateVertex(startNode, goalNode, top, false);
            } else {
                top.setgValue(MAX_VALUE);
                callUpdateVertex(startNode, goalNode, top, true);
            }
            if (PRIORITY_QUEUE.isEmpty())
                return false;
        }
        return true;
    }

    private static void callUpdateVertex(Node startNode, Node goalNode,
                                         Node top, boolean flag) {
        for (int i = 0; i< DX.length; i++) {
            int x = top.getPosX()+ DX[i];
            int y = top.getPosY()+ DY[i];
            updateVertexCommonCode(startNode, goalNode, x, y);
        }
        if (flag) {
            int x = top.getPosX();
            int y = top.getPosY();
            updateVertexCommonCode(startNode, goalNode, x, y);
        }
    }

    private static void updateVertexCommonCode(Node startNode, Node goalNode,
                                               int x, int y) {
        NodeManager nm = NodeManager.getInstance();
        Node node;
        String key = x+" "+y;

        if (isValid(x,y)) {
            if (!nm.containsNode(key)) {
                node = new Node(x, y);
                setValuesForNode(node, startNode);
                nm.addNode(node);
            } else {
                node = nm.getNode(key);
            }
            updateVertex(node, startNode, goalNode);
        }
    }

    private static boolean isValid(int x, int y) {
        return x>=0 && x<BOARD_SIZE && y>=0 && y<BOARD_SIZE;
    }

    private static void updateVertex(Node node, Node startNode, Node goalNode) {
        NodeManager nm = NodeManager.getInstance();
        if (!node.equals(goalNode)) {
            double minCost = Integer.MAX_VALUE;

            for (int i = 0; i < DX.length; i++) {
                int x = node.getPosX() + DX[i];
                int y = node.getPosY() + DY[i];
                String key = x + " " + y;

                if (isValid(x,y)) {
                    if (!nm.containsNode(key)) {
                        Node successorNode = new Node(x, y);
                        setValuesForNode(successorNode, startNode);
                        nm.addNode(successorNode);
                    }
                    minCost = Math.min(minCost,
                            calculateCost(node, nm.getNode(key)) +
                                    nm.getNode(key).getgValue());
                }
            }

            minCost = groundValue(minCost);
            node.setRhsValue(minCost);
        }

        if (PRIORITY_QUEUE.contains(node)){
            PRIORITY_QUEUE.remove(node);
        }

        if (node.getgValue() != node.getRhsValue()) {
            setValuesForNode(node, startNode);
            PRIORITY_QUEUE.add(node);
        }
    }

    private static double calculateCost(Node n1, Node n2) {
        if (BOARD[n1.getPosX()][n1.getPosY()].equals("O") ||
                BOARD[n2.getPosX()][n2.getPosY()].equals("O")) {
            return MAX_MAX_VALUE;
        }

        if (n1.getPosX() != n2.getPosX() && n1.getPosY() != n2.getPosY()) {
            return 1.4;
        } else
            return 1.0;
    }

    private static int calculateHeuristic(Node currentNode, Node startNode) {
        return Math.abs(currentNode.getPosX() - startNode.getPosX())
                + Math.abs(currentNode.getPosY() - startNode.getPosY());
    }

    private static Pair<Node,Node> findStartGoalNodes() {
        Node startNode = new Node();
        Node goalNode = new Node();

        for (int i=0;i<BOARD_SIZE;i++) {
            for (int j=0;j<BOARD_SIZE;j++) {
                if (BOARD[i][j].equals("S")) {
                    startNode = new Node(i,j);
                    setValuesForNode(startNode, startNode);
                }
                if (BOARD[i][j].equals("G")) {
                    goalNode = new Node(i,j);
                    goalNode.setRhsValue(0.0);
                    setValuesForNode(goalNode, startNode);
                }
            }
        }

        NodeManager nm = NodeManager.getInstance();
        nm.addNode(startNode);
        nm.addNode(goalNode);
        PRIORITY_QUEUE.add(goalNode);

        return new Pair<Node, Node>(startNode, goalNode) {};
    }

    private static void initializeBoard() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the size of the Board");
        BOARD_SIZE = scanner.nextInt();
        BOARD = new String[BOARD_SIZE][BOARD_SIZE];

        int p = 1;
        for (int i=0;i<BOARD_SIZE;i++) {
            for (int j=0;j<BOARD_SIZE;j++) {
                BOARD[i][j] = Integer.toString(p++);
            }
        }

        System.out.println("Initial Positions");
        printBoard();

        System.out.println("Enter the cell number for the Start and Goal");
        int st = scanner.nextInt();
        int go = scanner.nextInt();
        BOARD[(st-1)/BOARD_SIZE][(st-1)%BOARD_SIZE] = "S";
        BOARD[(go-1)/BOARD_SIZE][(go-1)%BOARD_SIZE] = "G";

        System.out.println("Enter the number of obstacles");
        int obstacleCount = scanner.nextInt();
        int ob;

        for (int i=0;i<obstacleCount;i++) {
            System.out.println("Enter the cell number for the Obstacles");
            ob = scanner.nextInt();
            BOARD[(ob-1)/BOARD_SIZE][(ob-1)%BOARD_SIZE] = "O";
        }
    }

    private static void printBoard() {
        String str = String.join("", Collections.nCopies(BOARD.length*16, "-"));
        System.out.println(str);

        for (int i=0;i<BOARD_SIZE;i++) {
            for (int j=0;j<BOARD_SIZE-1;j++) {
                if (BOARD[i][j].matches("\\d+"))
                    System.out.print("|\t" + BOARD[i][j] + "\t");
                else
                    System.out.print("|\t" + BOARD[i][j] + "\t");
            }
            if (BOARD[i][BOARD_SIZE-1].matches("\\d+"))
                System.out.print("|\t"+ BOARD[i][BOARD_SIZE-1] +"\t|");
            else
                System.out.print("|\t"+ BOARD[i][BOARD_SIZE-1] + "\t|");
            System.out.println();
            System.out.println(str);
        }
    }
}