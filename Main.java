package search;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

interface SearchingStrategy {
    Set<Integer> performSearch(Map<String, Set<Integer>> wordLineMap, String searchString);
}

class Searcher {
    SearchingStrategy searchingStrategy;
    public Searcher(SearchingStrategy searchingStrategy)  {
        this.searchingStrategy = searchingStrategy;
    }

    public Set<Integer> search(Map<String, Set<Integer>> wordLineMap, String searchString) {
        return searchingStrategy.performSearch(wordLineMap, searchString);
    }
}

class AllSearchStrategy implements SearchingStrategy {
    @Override
    public Set<Integer> performSearch(Map<String, Set<Integer>> wordLineMap, String searchString) {
        Set<Integer> linesIndex = new HashSet<>();
        for (String string : searchString.split(" ")) {
            if (linesIndex.isEmpty()) {
                linesIndex = wordLineMap.get(string);
            } else {
                linesIndex.retainAll(wordLineMap.get(string));
            }
        }
        return linesIndex;
    }
}

class AnySearchStrategy implements SearchingStrategy {
    public Set<Integer> performSearch(Map<String, Set<Integer>> wordLineMap, String searchString) {
        Set<Integer> linesIndex = new HashSet<>();
        for (String searchWord: searchString.split(" ")) {
            linesIndex.addAll(wordLineMap.get(searchWord));
        }
        return linesIndex;
    }
}

class NoneSearchStrategy implements SearchingStrategy {
    public Set<Integer> performSearch(Map<String, Set<Integer>> wordLineMap, String searchString) {
        boolean stringContainsSearchWord;
        int index = 0;
        Set<Integer> linesIndex = new HashSet<>();
        for (String searchWord: searchString.split(" ")) {
            linesIndex.addAll(wordLineMap.get(searchWord));
        }
        Set<Integer> allLines = new HashSet<>();
        wordLineMap.forEach((s, integers) -> allLines.addAll(integers));
        allLines.removeAll(linesIndex);
        return allLines;
    }
}

class InvertedIndexBuilder {
    public static Map<String, Set<Integer>> buildInvertedIndex(ArrayList<String> linesToIndex) {
        ArrayList<String> wordArray = new ArrayList<String>();
        Map<String, Set<Integer>> wordLineMap = new HashMap<String, Set<Integer>>();
        for (String line: linesToIndex) {
            wordArray.addAll(List.of(line.split(" ")));
        }
        for (String word: wordArray) {
            Set<Integer> wordPresentInLines = new HashSet<Integer>();
            int lineNumber = 0;
            for (String line:linesToIndex) {
                if (line.contains(word)){
                    wordPresentInLines.add(lineNumber);
                }
                lineNumber++;
            }
            wordLineMap.put(word, wordPresentInLines);
        }
        return wordLineMap;
    }
}

public class Main {
    public static void main(String[] args) {
        Searcher searcher = null;
        Map<String, Set<Integer>> wordLineMap;
        int selectedOption = -1;
        Scanner sc = new Scanner(System.in);
        ArrayList<String> fileLines = new ArrayList<String>();
        BufferedReader bufferedReader = null;
        try {
            String fileName = args[1];
            bufferedReader = new BufferedReader(new FileReader(fileName));
            String line;
            int fileLine = 0;
            while ((line = bufferedReader.readLine()) != null) {
                fileLines.add(line);
            }

        } catch (Exception e) {
            System.out.println("Error");
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                bufferedReader.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        wordLineMap = InvertedIndexBuilder.buildInvertedIndex(fileLines);
        while (selectedOption!= 0) {
            System.out.println("=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");
            selectedOption = sc.nextInt();
            sc.nextLine();
            switch (selectedOption) {
                case 0:
                    System.out.println("Bye!");
                    break;
                case 1:
                    System.out.println("Select a matching strategy: ALL, ANY, NONE");
                    String matchingStrategy = sc.nextLine().trim();
                    System.out.println("Enter a name or email to search all suitable people.");
                    String searchString = sc.nextLine();
                    if (matchingStrategy.equals("ALL")) {
                        searcher = new Searcher(new AllSearchStrategy());
                    } else if (matchingStrategy.equals("NONE")) {
                        searcher = new Searcher(new NoneSearchStrategy());
                    } else if (matchingStrategy.equals("ANY")) {
                        searcher = new Searcher(new AnySearchStrategy());
                    }
                    searcher.search(wordLineMap, searchString).forEach(integer -> System.out.println(fileLines.get(integer)));
                    break;
                case 2:
                    System.out.println("=== List of people ===");
                    for (String line : fileLines) {
                        if (line != null) {
                            System.out.println(line);
                        }
                    }
                    break;
                default:
                    System.out.println("Incorrect option! Try again.");
            }
        };
    }
}
