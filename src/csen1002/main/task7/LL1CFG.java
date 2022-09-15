package csen1002.main.task7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Write your info here
 * 
 * @name Mostafa Mohamed Abdelnasser
 * @id 43-8530
 * @labNumber 11
 */
public class LL1CFG {
	/**
	 * LL1 CFG constructor
	 * 
	 * @param description is the string describing an LL(1) CFG, first, and follow as represented in the task description.
	 */
	HashMap<String, ArrayList> CFG = new HashMap<>();
	HashMap<String, ArrayList> First = new HashMap<>();
	HashMap<String, String> Follow = new HashMap<>();
	HashMap<String, String> parsingTable = new HashMap<>();
	ArrayList<String> varIdx = new ArrayList<>();
	ArrayList<String> termIdx = new ArrayList<>();
	public LL1CFG(String description) {
		//Parse the input
		String[] hashSeparated = description.split("#");
		String[] rules = hashSeparated[0].split(";");
		String[] firsts = hashSeparated[1].split(";");
		String[] follows = hashSeparated[2].split(";");

		for(String rule : rules){
			String[] letters = rule.split(",");
			String variable = letters[0];
			ArrayList<String> temp = new ArrayList<>();
			for(int i = 1;i < letters.length;i++){
				temp.add(letters[i]);
				String [] splitRule = letters[i].split("");
				for(String l : splitRule)
					if((Character.isLowerCase(l.charAt(0)) || l.equals("$"))&& !termIdx.contains(l))
						termIdx.add(l);
			}
			varIdx.add(variable);
			CFG.put(variable,temp);
		}

		for(String first : firsts){
			String[] letters = first.split(",");
			String variable = letters[0];
			ArrayList<String> temp = new ArrayList<>();
			for(int i = 1;i < letters.length;i++){
				temp.add(letters[i]);
			}
			First.put(variable,temp);
		}

		for(String follow : follows){
			String[] letters = follow.split(",");
			Follow.put(letters[0],letters[1]);
		}

		//Now we construct the table
		termIdx.add("$");
		for(String var : varIdx)
			for(String terminal : termIdx){
				String key = var + terminal;
				ArrayList<String> varRules = CFG.get(var);
				ArrayList<String> varFirsts = First.get(var);
				String varFollows = Follow.get(var);
 				for(int i = 0;i < varRules.size();i++){
					 String currRule = varRules.get(i);
					 String currFirst = varFirsts.get(i);
					 // Case the first of this rule has the terminal in its set
					 if((currFirst.contains(terminal))
					// Case the first set contains epsilon
					// and the follow of the variable contains the terminal
					 || currFirst.contains("e") && varFollows.contains(terminal)){
						 parsingTable.put(key,currRule);
						 break;
					 }
				}
			}
	}
	/**
	 * Returns A string encoding a derivation is a comma-separated sequence of sentential forms each representing a step in the derivation..
	 * 
	 * @param input is the string to be parsed by the LL(1) CFG.
	 * @return returns a string encoding a left-most derivation.
	 */
	public String parse(String input) {
		String output = "";
		// Mark the end of input
		input+="$";
		String[] inputArray = input.split("");
		// Create a stack
		Stack<String> stack = new Stack<>();
		// Push the $ and the start variable
		stack.push("$");
		stack.push("S");
		output += "S";
		// Initialize a pointer to parse the input
		int i = 0;
		// Pointer to track the retarded output format
		int j = 0;
		// Begin the automaton's loop
		while(!stack.isEmpty() || i > input.length()){
			// Peek at the stack
			String top = stack.peek();
			// Case it's a terminal
			if(Character.isLowerCase(top.charAt(0)) || top.equals("$"))
				// If the pointer points to it in the input string
				if(inputArray[i].equals(top)){
					// Pop the stack
					stack.pop();
					// And move the pointer
					i++;
					// And add the step to the output
					j++;
				}
				// We have a terminal that is not
				// the one we're pointing at
				else{
					// Print out an error
					output += ",ERROR";
					break;
				}
			// Case it's a variable
			else {
				// Create the hashmap key to the parsing table
				String key = top + inputArray[i];
				// Get the corresponding rule in the table
				String rule = parsingTable.get(key);
				// If there's no such rule
				if(rule == null){
					// Print out an error
					output += ",ERROR";
					break;
				}
				// If there's a rule
				else {
					// Pop the variable from the stack
					String popped = stack.pop();
					// Add its reverse to the stack
					String[] ruleArray = rule.split("");
					for(int n = ruleArray.length-1;n >= 0;n--){
						stack.push(ruleArray[n]);
					}
					// Add the step to the output
					String[] splitOut = output.split(",");
					String[] editedRule = splitOut[splitOut.length-1].split("");
					editedRule[j] = rule;
					if(rule.equals("e")) {
						editedRule[j] = "";
						stack.pop();
					}
					output += "," + String.join("",editedRule);
				}
			}
		}


		return output;
	}

}
