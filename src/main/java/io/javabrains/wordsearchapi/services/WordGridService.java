package io.javabrains.wordsearchapi.services;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Scope;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Scope("prototype")
public class WordGridService {

    private enum Direction {
        HORIZONTAL,
        VERTICAL,
        DIAGONAL,
        HORIZONTAL_INVERSE,
        VERTICAL_INVERSE,
        DIAGONAL_INVERSE,
    }

    private class Coordinate {
        int x;
        int y;
        Coordinate(int x,int y){
            this.x = x;
            this.y = y;
        }
    }

    public char[][] generateGrid(int gridSize, List<String> words) {
        List<Coordinate> coordinates = new ArrayList<>();
        char[][] content = new char[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                coordinates.add(new Coordinate(i,j));
                content[i][j] = '_';
            }
        }

        for (String word: words) {
            Collections.shuffle(coordinates);
            word = word.toUpperCase();
            for (Coordinate coordinate : coordinates) {
                int x = coordinate.x;
                int y = coordinate.y;
                Direction selectedDirection = getDirectionForFit(content,word,coordinate);
                if (selectedDirection != null) {
                    switch(selectedDirection) {
                        case HORIZONTAL -> {
                            for (char c : word.toCharArray()) {
                                content[x][y++] = c;
                            }
                        }
                        case VERTICAL -> {
                            for (char c : word.toCharArray()) {
                                content[x++][y] = c;
                            }
                        }
                        case DIAGONAL -> {
                            for (char c : word.toCharArray()) {
                                content[x++][y++] = c;
                            }
                        }
                        case HORIZONTAL_INVERSE -> {
                            for (char c : word.toCharArray()) {
                                content[x][y--] = c;
                            }
                        }
                        case VERTICAL_INVERSE -> {
                            for (char c : word.toCharArray()) {
                                content[x--][y] = c;
                            }
                        }
                        case DIAGONAL_INVERSE -> {
                            for (char c : word.toCharArray()) {
                                content[x--][y--] = c;
                            }
                        }
                    }
                }
                break;
            }
        }
        randomFillGrid(content);
        return content;
    }

    public void displayGrid(char[][] content){
        int gridSize = content[0].length;
        for (int i = 0; i< gridSize; i++) {
            for (int j=0; j<gridSize; j++) {
                System.out.print(content[i][j] + " ");
            }
            System.out.println("");
        }
    }

    private void randomFillGrid(char[][] content){
        int gridSize = content[0].length;
        String allCapLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i< gridSize; i++) {
            for (int j=0; j<gridSize; j++) {
                if (content[i][j] == '_') {
                    int randomIndex = ThreadLocalRandom.current().nextInt(0,allCapLetters.length());
                    content[i][j] = allCapLetters.charAt(randomIndex);
                }
            }
        }
    }

    private Direction getDirectionForFit(char[][] content, String word, Coordinate coordinate) {
        List<Direction> directions = Arrays.asList(Direction.values());
        Collections.shuffle(directions);
        int gridSize = content[0].length;
        for (Direction direction : directions) {
            if (doesFit(content, word, coordinate, direction)) {
                return direction;
            }
        }
        return null;

    }

    private boolean doesFit(char[][] content, String word, Coordinate coordinate, Direction direction) {
        int wordLength = word.length();
        int y = coordinate.y;
        int x = coordinate.x;
        int gridSize = content[0].length;
        switch (direction) {
            case HORIZONTAL -> {
                if (y + wordLength > gridSize) return false;
                for (int i = 0; i < wordLength; i++) {
                    if (content[x][y + i] != '_') return false;
                }
            }
            case VERTICAL -> {
                if (x + wordLength > gridSize) return false;
                for (int i = 0; i < wordLength; i++) {
                    if (content[x + i][y] != '_') return false;
                }
            }
            case DIAGONAL -> {
                if (x + wordLength > gridSize || y + wordLength > gridSize) return false;
                for (int i = 0; i < wordLength; i++) {
                    if (content[x + i][y + i] != '_') return false;
                }
            }
            case HORIZONTAL_INVERSE -> {
                if (coordinate.y < wordLength) return false;
                for (int i = 0; i < wordLength; i++) {
                    if (content[x][y - i] != '_') return false;
                }
            }
            case VERTICAL_INVERSE -> {
                if (coordinate.x < wordLength) return false;
                for (int i = 0; i < wordLength; i++) {
                    if (content[x - i][y] != '_') return false;
                }
            }
            case DIAGONAL_INVERSE -> {
                if (coordinate.x < wordLength || coordinate.y < wordLength) return false;
                for (int i = 0; i < wordLength; i++) {
                    if (content[x - i][y - i] != '_') return false;
                }
            }
        }
        return true;
    }

}
