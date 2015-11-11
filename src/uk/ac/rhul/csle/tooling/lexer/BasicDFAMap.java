/**
 * 
 */
package uk.ac.rhul.csle.tooling.lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a basic finite-state automaton implementation of
 * DFAMap. This implementation assumes a hand-written specificaton and should
 * not be taken to be a model implementation.
 * <p>
 * A class extending this abstract class must initialise TOKENS, KEYWORDS and
 * LAYOUTTOKENS, as well as define the initialise() method.
 *
 * @author Robert Michael Walsh
 *
 */
public abstract class BasicDFAMap implements DFAMap {

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return DFAMap.toString();
  }

  /**
   * An internal class to represent a DFA data structure
   *
   * @author Robert Michael Walsh
   *
   */
  protected class DFA {

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      String tmp = "";

      for (int i = 0; i < transitionTable.length; ++i) {
        tmp += i + " -> " + transitionTable[i] + "\n";
      }

      return tmp;
    }

    /**
     * An internal class representing a state in a DFA
     *
     * @author Robert Michael Walsh
     *
     */
    private class State {

      /*
       * (non-Javadoc)
       * 
       * @see java.lang.Object#toString()
       */
      @Override
      public String toString() {
        return transitions.toString();
      }

      /**
       * Determines whether the state is an accepting state
       */
      private boolean isAccepting;

      /**
       * A list of transitions for this state. This is equivalent to the list of
       * out-edges for the node labelled with this state.
       */
      private final ArrayList<Transition> transitions;

      /**
       * Constructs a non-accepting state with no transitions/out-edges
       */
      public State() {
        transitions = new ArrayList<>();
        isAccepting = false;
      }

      /**
       * Adds a new transition/out-edge for this state.
       * 
       * @param transition
       *          The transition/out-edge to add
       */
      public void addTransition(Transition transition) {
        transitions.add(transition);
      }

      /**
       * Returns the list of transition/out-edges for this state. This is
       * intended solely to allow iteration over the list of transitions for the
       * purpose for searching for transitions to apply. Therefore, the returned
       * list is read-only.
       * 
       * @return A read-only view of the list of transitions/out-edges
       */
      public List<Transition> getTransitions() {
        return Collections.unmodifiableList(transitions);
      }

      /**
       * Returns whether or not the state is an accepting state
       * 
       * @return true if the state is accepting, and false otherwise
       */
      public boolean isAccepting() {
        return isAccepting;
      }

      /**
       * Turns this state into an accepting state.
       */
      public void setAccepting() {
        isAccepting = true;
      }
    }

    /**
     * An internal class representing a transition/out-edge. For this
     * implementation, it is assumed that there is a from state maintaining a
     * list of Transition objects. Therefore this class stores only the set of
     * characters that are the second operand of the transition function (or the
     * label of the out-edge), and the resulting state (the end node).
     * 
     * @author robert
     *
     */
    private class Transition {

      /*
       * (non-Javadoc)
       * 
       * @see java.lang.Object#toString()
       */
      @Override
      public String toString() {
        return "(" + to + "," + charSet.toString() + "," + largeUnicodeSetCase + ")";
      }

      /**
       * The set of characters that are operands to this transition function.
       * Alternatively if <code>largeUnicodeSetCase</code> is true, then this is
       * the set of characters that are NOT operands to this transition
       * function.
       */
      private final Set<Character> charSet;

      /**
       * A flag that when set to true, specifies that the set of characters
       * associated with this transition are the set of characters that are not
       * operands to this transition function.
       * <p>
       * This is designed for cases where a transition can accept more
       * characters than it rejects.
       */
      private final boolean largeUnicodeSetCase;

      /**
       * The number of the end state that should result from a successful
       * transition
       */
      private final int to;

      /**
       * Constructs a new transition whose end state is <code>to</code>. If
       * <code>isMatchingOnLargeSetOfCharacters</code> is false, then
       * <code>characters</code> is the set of characters that are operands to
       * this transition function (the label of the transition edges), otherwise
       * <code>characters</code> is the set of characters that are NOT operands
       * to this transition function.
       * 
       * @param to
       *          The resulting state for this transition
       * @param isMatchingOnLargeSetOfCharacters
       *          Should be false if the transition should accept any character
       *          in <code>characters</code>, and should be true if the
       *          transition should accept any character EXCEPT those in
       *          <code>characters</code>.
       * @param characters
       *          The set of characters that are accepted by this transition if
       *          <code>isMatchingOnLargeSetOfCharacters</code> is false. Is the
       *          set of characters that are rejected by this transition if
       *          <code>isMatchingOnLargeSetOfCharacters</code> is true.
       */
      public Transition(int to, boolean isMatchingOnLargeSetOfCharacters, char... characters) {
        charSet = new HashSet<>();
        for (final char c : characters) {
          charSet.add(c);
        }
        largeUnicodeSetCase = isMatchingOnLargeSetOfCharacters;
        this.to = to;
      }

      /**
       * Constructs a new transition whose end state is <code>to</code> and
       * whose set of characters that are operands to this transition function
       * (the label of the transition edges) is <code>characters</code>
       * 
       * @param to
       *          The resulting state for this transition
       * @param characters
       *          The set of characters that are accepted by this transition
       */
      public Transition(int to, char... characters) {
        charSet = new HashSet<>();
        for (final char c : characters) {
          charSet.add(c);
        }
        largeUnicodeSetCase = false;
        this.to = to;
      }

      /**
       * Determines whether a transition can be applied, given an input
       * character, and applies this transition if so.
       * <p>
       * If <code>largeUnicodeSetCase</code> is true then this function returns
       * the end state if <code>Character.isDefined(input)</code> returns true
       * and <code>input<code> is not in the set of characters for this
       * transition.
       * <p>
       * If a transition cannot be applied, then this function returns -1.
       * 
       * @param input
       *          The character to test.
       * @return the end state of this transition if a transition can be
       *         applied, and -1 otherwise.
       */
      public int apply(char input) {
        if (largeUnicodeSetCase) {
          if (Character.isDefined(input) && !charSet.contains(input)) {
            return to;
          }
        } else {
          if (charSet.contains(input)) {
            return to;
          }
        }
        return -1;
      }
    }

    /**
     * Stores the number of the state that this DFA is currently in.
     */
    int currentState;

    /**
     * Stores the set of states in this DFA
     */
    final State[] transitionTable;

    /**
     * Construct a DFA containing <count>noOfState</count> states
     * 
     * @param noOfStates
     *          The number of states in this automaton
     */
    public DFA(int noOfStates) {
      currentState = 0;
      transitionTable = new State[noOfStates];
      for (int i = 0; i < transitionTable.length; ++i) {
        transitionTable[i] = new State();
      }
    }

    /**
     * Sets the given state to be accepting. Does nothing if the state is
     * already accepting.
     * 
     * @param state
     *          That state that should become accepting
     */
    public void addAcceptingState(int state) {
      transitionTable[state].setAccepting();
    }

    /**
     * Specifies that the state given by <code>from</code> should have a
     * transition function which takes <code>from</code> to <code>to</code> on
     * any character that is NOT one of the characters in
     * <code>characters</code>
     * 
     * @param from
     *          The state for which this transition should be defined
     * @param to
     *          The end state if the transition is successful
     * @param characters
     *          A list of the characters that should not result in a successful
     *          transition
     */
    public void addAllUnicodeTransition(int from, int to, char... characters) {
      transitionTable[from].addTransition(new Transition(to, true, characters));
    }

    /**
     * 
     * Specifies that the state given by <code>from</code> should have a
     * transition function which takes <code>from</code> to <code>to</code> on
     * any character that is one of the characters in <code>characters</code>
     * 
     * @param from
     *          The state for which this transition should be defined
     * @param to
     *          The end state if the transition is successful
     * @param characters
     *          A list of the characters that should result in a successful
     *          transition
     */
    public void addTransition(int from, int to, char... characters) {
      transitionTable[from].addTransition(new Transition(to, characters));
    }

    /**
     * Given an input character, attempts to apply a transition from the current
     * state. If there exists a transition from the current state on
     * <code>input</input> to some new state, t, then t becomes the new current
     * state and the function returns true.
     * <p>
     * If there is no valid transition then the function returns false
     * 
     * @param input
     *          The input character to test
     * @return True if a transition was applied, and false otherwise
     */
    public boolean applyTransition(char input) {
      boolean canTransition = false;
      for (final Transition transition : transitionTable[currentState].getTransitions()) {
        final int next = transition.apply(input);
        if (next != -1) {
          canTransition = true;
          currentState = next;
          break;
        }
      }
      return canTransition;
    }

    /**
     * Returns true if the DFA is currently in an accepting state
     * 
     * @return True if the current state is accepting, and false otherwise
     */
    public boolean atAcceptingState() {
      return transitionTable[currentState].isAccepting();
    }

    /**
     * Sets the current state of this NFA to state 0
     */
    public void resetCurrentState() {
      currentState = 0;
    }
  }

  /**
   * The set of tokens which correspond to a single lexeme
   */
  protected Set<String> keywords;

  /**
   * The list of tokens that correspond to layout (or whitespace)
   */
  protected String[] LAYOUTTOKENS;

  /**
   * The full list of tokens
   */
  protected String[] TOKENS;

  /**
   * A map storing a DFA for each token, the key is the name of the token and
   * the value is the DFA associated with it
   */
  protected final Map<String, DFA> DFAMap;

  /**
   * Constructs a new BasicDFAMap instance and initialises all the Tokens DFAs
   */
  public BasicDFAMap() {
    DFAMap = new HashMap<>();
    initialise();
  }

  @Override
  public boolean applyTransition(String token, char input) {
    if (!DFAMap.containsKey(token)) {
      System.err.println("DFA does not exist.");
      System.err.print(Arrays.toString(new Throwable().getStackTrace()));
      return false;
    }
    final DFA nfa = DFAMap.get(token);

    return nfa.applyTransition(input);
  }

  @Override
  public boolean atAcceptingState(String token) {
    if (!DFAMap.containsKey(token)) {
      System.err.println("DFA does not exist.");
      System.err.print(Arrays.toString(new Throwable().getStackTrace()));
      return false;
    }
    return DFAMap.get(token).atAcceptingState();
  }

  @Override
  public String getKeywordString(String token) {
    return token;
  }

  @Override
  public String[] getLayouttokens() {
    return LAYOUTTOKENS;
  }

  @Override
  public String[] getTokens() {
    return TOKENS;
  }

  /**
   * Private function initialising all the tokens and DFAs in this specification
   */
  protected abstract void initialise();

  @Override
  public boolean isKeyword(String token) {
    return keywords.contains(token);
  }

  @Override
  public boolean isLayout(String token) {
    for (final String keyword : LAYOUTTOKENS) {
      if (token.equals(keyword)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void reset(String token) {
    if (!DFAMap.containsKey(token)) {
      System.err.println("DFA does not exist.");
      System.err.print(Arrays.toString(new Throwable().getStackTrace()));
    } else {
      DFAMap.get(token).resetCurrentState();
    }
  }
}
