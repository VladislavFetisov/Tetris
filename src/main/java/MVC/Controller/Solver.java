package MVC.Controller;

import MVC.Model.Coord;
import MVC.Model.Field;
import MVC.Model.Pair;
import MVC.Model.RotationMode;

import java.util.*;

import static MVC.Model.ShiftDirection.LEFT;
import static MVC.Model.ShiftDirection.RIGHT;

public class Solver {
    private Coord[] bestCoord;
    private RotationMode bestRotation;
    private final Coord initial = new Coord(4, Field.getCeil());

    public Coord[] getBestCoord() {
        return bestCoord;
    }

    public RotationMode getBestRotation() {
        return bestRotation;
    }

    public void solve(Field board, int height) {
        int minPenalty = 100000;
        int holesCreated, holeHeight, heightAdded, lineCleared;

        // line cleared соответсвует c коэффициенту с отрицательным весом


        for (int j = 0; j < 4; j++) {//перебираем rotation

            RotationMode currentRotation = board.getFigure().getRotation();

            while (board.tryShiftFigure(LEFT)) {
                System.out.println(board.toString());
            }

            for (int k = 0; k < Field.getWidth(); k++) {//идем по всей ширине поля вправо

                heightAdded = height;
                System.out.println(height+"height");
                while (board.tryFallFigure()) {//опускаем фигуру до возможного
                    System.out.println(board.toString());
                }

                if (board.getFigureMaxY() > heightAdded) {//определили добавленную высоту
                    heightAdded = board.getFigureMaxY() - heightAdded;
                } else heightAdded = 0;

                Pair pair = board.getAmountOfHoles();
                holesCreated = pair.getFirst();
                holeHeight = pair.getSecond();
                lineCleared = board.amountOfSupposingDestLines();
                System.out.println(holeHeight + " hh" + holesCreated + " hc" + lineCleared + " lc" + heightAdded + " ha");

                int currentMinPenalty = geneticAlgorithm(holesCreated, holeHeight, lineCleared, heightAdded);
                System.out.println(currentMinPenalty+"currentMinPenalty");
                if (currentMinPenalty < minPenalty) {
                    minPenalty = currentMinPenalty;
                    bestRotation = currentRotation;
                    bestCoord = board.getFigure().getCoord();
                }

                while (board.tryElevateFigure()) { //подняли фигуру наверх
                }
                System.out.println(board.toString());
                if (!board.tryShiftFigure(RIGHT)) {
                    board.clearFigureOnDesk(board.getFigure().getCoord());
                    break;
                }

            }
            board.setInitialFigure(RotationMode.getNextRotationFrom(currentRotation), board.getFigure().getFigureForm());
            System.out.println(board);
        }
    }

    private int geneticAlgorithm(int holesCreated, int holeHeight, int lineCleared, int heightAdded) {
        HashMap<Chromosome, Integer> generationWithFitness = new HashMap<>();
        int max = 100000;
        for (int i = 0; i < 100; i++) {//начальное поколение из 100 особей
            generationWithFitness.put(new Chromosome((int) Math.floor(Math.random() * 500),
                    (int) Math.floor(Math.random() * 500),
                    (int) Math.floor(Math.random() * -500),
                    (int) Math.floor(Math.random() * 500)), 0);//положили хромосому с начальным fitness
        }
        int j;
        ArrayList<Integer> sortedFitnessList = new ArrayList<>();
        ArrayList<Chromosome> bests = new ArrayList<>(30);

        for (int i = 0; i < 1; i++) {//повторяем цикл 50 раз
            for (Map.Entry<Chromosome, Integer> entry : generationWithFitness.entrySet()) {//имеем 100 особей со своим fitness
                generationWithFitness.replace(entry.getKey(), entry.getKey().fitness(holesCreated, holeHeight, lineCleared, heightAdded));
            }


            for (Map.Entry<Chromosome, Integer> entry : generationWithFitness.entrySet()) {
                sortedFitnessList.add(entry.getValue());
            }

            insertionSort(sortedFitnessList);


            if (sortedFitnessList.get(0) < max) max = sortedFitnessList.get(0);//выбрали лучшую хромосому


            j = 0;
            for (Map.Entry<Chromosome, Integer> entry : generationWithFitness.entrySet()) {
                if (sortedFitnessList.indexOf(entry.getValue()) < 30) {
                    bests.add(entry.getKey());
                    j++;
                }
                if (j == 30) break;
            }

            ArrayList<Chromosome> newGeneration = Chromosome.crossBreeds(bests, 100);
            Chromosome.mutation(newGeneration);

            j = 0;
            for (Map.Entry<Chromosome, Integer> entry : generationWithFitness.entrySet()) {
                if (sortedFitnessList.indexOf(entry.getValue()) > 70) {
                    newGeneration.remove((int) (Math.random() * (newGeneration.size())));
                    newGeneration.add(entry.getKey()); //заполнили поколение 30 самыми слабыми родителями
                    j++;
                }
                if (j == 30) break;
            }


            for (Chromosome chromosome : newGeneration) generationWithFitness.put(chromosome, 0);

            bests.clear();
            sortedFitnessList.clear();

        }
        return max;
    }

    public static void insertionSort(List<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            int current = list.get(i);
            int j = i - 1;
            for (; j >= 0; j--) {
                if (list.get(j) > current) list.set(j + 1, list.get(j));
                else break;
            }
            list.set(j + 1, current);
        }
    }

    private static class Chromosome {
        private int a, b, c, d;
        private static final int PARAMETERS_COUNT = 4;

        public Chromosome(int a, int b, int c, int d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }


        public int fitness(int holesCreated, int holeHeight, int lineCleared, int heightAdded) {
            return a * heightAdded + b * holesCreated + c * lineCleared + d * holeHeight;
        }

        public static ArrayList<Chromosome> crossBreeds(List<Chromosome> list, int countOfChildren) {
            ArrayList<Chromosome> result = new ArrayList<>();
            for (int i = 0; i < countOfChildren; i++) {
                Chromosome firstParent = list.get((int) (Math.random() * (list.size())));
                Chromosome secondParent = list.get((int) (Math.random() * (list.size())));
                while (secondParent.equals(firstParent)) {
                    secondParent = list.get((int) (Math.random() * (list.size())));
                }
                result.add(firstParent.crossover(secondParent));
            }
            return result;
        }

        public static void mutation(List<Chromosome> generation) {//дает шанс увеличить или уменьшить
            for (Chromosome chromosome : generation) {
                int chance = (int) (Math.random() * (generation.size()));
                if (chance < 5) {
                    int coefficient = (int) (Math.random() * PARAMETERS_COUNT + 1);
                    int incOrDec = (int) (Math.random() * 2);
                    switch (coefficient) {
                        case 0:
                            if (incOrDec == 1) chromosome.a += 10;
                            else chromosome.a -= 10;
                            break;
                        case 1:
                            if (incOrDec == 1) chromosome.b += 10;
                            else chromosome.b -= 10;
                            break;
                        case 2:
                            if (incOrDec == 1) chromosome.c += 10;
                            else chromosome.c -= 10;
                            break;
                        case 3:
                            if (incOrDec == 1) chromosome.d += 10;
                            else chromosome.d -= 10;
                            break;
                    }
                }
            }
        }

        private Chromosome crossover(Chromosome other) {
            switch ((int) (Math.random() * PARAMETERS_COUNT - 1)) {
                case 0:
                    return new Chromosome(a, other.b, other.c, other.d);
                case 1:
                    return new Chromosome(a, b, other.c, other.d);
                case 2:
                    return new Chromosome(a, b, c, other.d);

            }
            return new Chromosome(a, b, c, d);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Chromosome that = (Chromosome) o;
            return a == that.a && b == that.b && c == that.c && d == that.d;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b, c, d);
        }

        @Override
        public String toString() {
            return "Chromosome{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    ", d=" + d +
                    '}';
        }
    }
}