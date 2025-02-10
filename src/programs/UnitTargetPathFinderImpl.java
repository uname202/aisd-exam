package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    /**
     * Алгоритмическая сложность:
     * 1. Создание множества препятствий: O(n), где n - количество юнитов
     * 2. Основной алгоритм Дейкстры:
     *    - Приоритетная очередь: O(log V) для вставки/извлечения, где V = WIDTH * HEIGHT
     *    - Каждая вершина может быть добавлена в очередь максимум 4 раза (по числу направлений)
     *    - Итого: O((WIDTH * HEIGHT) * log(WIDTH * HEIGHT))
     * Общая сложность: O((WIDTH * HEIGHT) * log(WIDTH * HEIGHT))
     */
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {

        Set<PathEdge> obstacles = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                obstacles.add(new PathEdge(unit.getxCoordinate(), unit.getyCoordinate()));
            }
        }

        PathEdge start = new PathEdge(attackUnit.getxCoordinate(), attackUnit.getyCoordinate());
        PathEdge end = new PathEdge(targetUnit.getxCoordinate(), targetUnit.getyCoordinate());

        Map<PathEdge, Integer> distances = new HashMap<>();
        Map<PathEdge, PathEdge> previousNode = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        Set<PathEdge> visited = new HashSet<>();

        distances.put(start, 0);
        queue.offer(new Node(start, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            PathEdge currentEdge = current.edge;

            if (currentEdge.equals(end)) {
                return reconstructPath(previousNode, end);
            }

            if (visited.contains(currentEdge)) {
                continue;
            }
            visited.add(currentEdge);

            for (int[] dir : DIRECTIONS) {
                PathEdge neighbor = new PathEdge(
                        currentEdge.getX() + dir[0],
                        currentEdge.getY() + dir[1]
                );

                if (isValidPosition(neighbor, obstacles)) {
                    int newDist = distances.get(currentEdge) + 1;

                    if (!distances.containsKey(neighbor) || newDist < distances.get(neighbor)) {
                        distances.put(neighbor, newDist);
                        previousNode.put(neighbor, currentEdge);
                        queue.offer(new Node(neighbor, newDist));
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private boolean isValidPosition(PathEdge pos, Set<PathEdge> obstacles) {
        return pos.getX() >= 0 && pos.getX() < WIDTH &&
                pos.getY() >= 0 && pos.getY() < HEIGHT &&
                !obstacles.contains(pos);
    }

    private List<Edge> reconstructPath(Map<PathEdge, PathEdge> previousNode, PathEdge current) {
        List<Edge> path = new ArrayList<>();
        path.add(current.toEdge());

        while (previousNode.containsKey(current)) {
            current = previousNode.get(current);
            path.add(0, current.toEdge());
        }

        return path;
    }

    private static class PathEdge {
        private final int x;
        private final int y;

        PathEdge(int x, int y) {
            this.x = x;
            this.y = y;
        }

        PathEdge(Edge edge) {
            this.x = edge.getX();
            this.y = edge.getY();
        }

        public int getX() { return x; }
        public int getY() { return y; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathEdge other = (PathEdge) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        public Edge toEdge() {
            return new Edge(x, y);
        }
    }

    private static class Node {
        PathEdge edge;
        int distance;

        Node(PathEdge edge, int distance) {
            this.edge = edge;
            this.distance = distance;
        }
    }
}
