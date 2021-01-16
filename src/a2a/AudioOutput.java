package a2a;

import java.util.Locale;
import java.util.Scanner;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

/**
 * Represents an AudioOutput which turns input given text into output audio
 * form.
 * 
 * @author Ava Oveisi
 */
public class AudioOutput extends Command {

  /**
   * Creates initial AudioOutput object by setting default instance variable
   * values from Command constructor
   * 
   * @param b Builder object to construct AudioOutput object
   */
  public AudioOutput(Builder b) {
    super(b);
  }

  /**
   * Represents Builder object to create AudioOutput object
   */
  public static class Builder extends Command.Builder {
    /**
     * Builds and returns AudioOutput object
     * 
     * @return AudioOutput object
     */
    public AudioOutput build() {
      return new AudioOutput(this);
    }
  }

  /**
   * Returns true if argument contained in "" and input is one liner or argument
   * ends with QUIT and input is more than one line.
   * 
   * @param args text input to be validated
   * @return true if args is a valid text input, false otherwise
   */
  @Override
  public boolean validateArgs(String args) {
    return ((args.trim().endsWith("\"") && args.trim().startsWith("\"")
        && args.trim().indexOf("\"") != args.trim().lastIndexOf("\"")
        && !args.trim().substring(1, args.trim().length() - 1).contains("\""))
        || args.trim().endsWith(" QUIT") || args.trim().equals("QUIT"));
  }

  /**
   * Stores text from single or multi-line input to be turned to audio format
   * 
   * @param arg text to be turned to audio format
   */
  @Override
  public void parseArgs(String arg) {
    this.parsedArgs.clear();
    if (arg.trim().startsWith("\"") && arg.trim().endsWith("\"")) {
      this.parsedArgs.add(arg.trim().substring(1, arg.trim().length() - 1));
    } else if (arg.trim().endsWith(" QUIT")) {
      this.parsedArgs.add(arg.trim().substring(0, arg.trim().length() - 5));
    } else {
      this.parsedArgs.add("");
    }
  }

  /**
   * Returns command and arguments if one line input is valid. Otherwise asks
   * for user input until getting valid argument, then returns command and
   * arguments.
   * 
   * @param args first line input
   * @return full valid user input which can be single or multi-lined
   */
  public String getInput(String args) {
    String quote = "\"";
    String resInpArgs = args + " ";
    // special case: error if first line input is QUIT
    if (args.equals("QUIT")) {
      printMessage.printInvalidString();
      return "speak " + resInpArgs;
    }
    // check if one liner input ends and starts with quotation, don't get
    // further
    // input if yes
    if (args.trim().isEmpty()) {
      String currInp = "";
      Scanner in = new Scanner(System.in);
      // multi-line input must end with QUIT
      while (!(validateArgs(resInpArgs) && !resInpArgs.endsWith(quote)
          && !currInp.trim().equals(quote)) && !currInp.trim().equals("QUIT")) {
        currInp = in.nextLine();
        resInpArgs += currInp + " ";
      }
    }
    return "speak " + resInpArgs;
  }

  /**
   * Outputs text given in audio format, if valid
   * 
   * @param args input text to be validated and turned to audio format
   */
  @Override
  public String run(String args) {
    if (validateArgs(args)) {
      parseArgs(args); // stores string to be spoken in this.args
      try {
        // Set property as Kevin Dictionary
        System.setProperty("freetts.voices",
            "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");
        // Register Engine
        Central.registerEngineCentral(
            "com.sun.speech.freetts" + ".jsapi.FreeTTSEngineCentral");

        // Create a Synthesizer
        Synthesizer synthesizer =
            Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
        synthesizer.allocate(); // Allocate synthesizer
        synthesizer.resume(); // Resume Synthesizer
        // Speaks the given text until the queue is empty.
        synthesizer.speakPlainText(this.parsedArgs.get(0), null);
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        // synthesizer.deallocate(); // Deallocate the Synthesizer.
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      printMessage.printInvalidString();
    }
    return "";
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of speak command
   * 
   * @return formatted information about speak command
   */
  public String toString() {
    String command = "SPEAK";
    String name = "\n\tspeak - convert text to audible speech";
    String synopsis = "\n\tspeak [STRING] ";
    String description =
        "\n\tGiven a string literal argument, turn string into audible speech."
            + "\n\tGiven no argument, turn all input proceeded by 'QUIT' "
            + "into audible speech. 'QUIT' must be a single word, not proceeded"
            + " by or followed by any characters.";
    String errors =
        "\n\tMaximum of 1 string literal argument containing no quotation as "
            + "its content is valid.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;
  }

}

// REFERENCE for Speak Run code
// https://www.geeksforgeeks.org/converting-text-speech-java/
