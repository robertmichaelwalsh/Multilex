package uk.ac.rhul.csle.tooling.parsing;

public class CSBuiltinParserAdditionalBuiltins extends CSBuiltinParser {

  private boolean isdigit(int i) {
    return Character.isDigit(i);
  }

  private boolean isxdigit(int i) {
    return isdigit(i) || (i >= 'a' && i <= 'f') || (i >= 'A' && i <= 'F');
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.rhul.csle.gll.GLLSupport#artBuiltin_INTEGER(int)
   */
  @Override
  protected int artBuiltin_INTEGER(int characterStringInputIndex) {
    int startIndex = characterStringInputIndex;

    /* Check for hexadecimal introducer */
    boolean hex =
            (characterStringInput.charAt(characterStringInputIndex) == '0' && (characterStringInput
                    .charAt(characterStringInputIndex + 1) == 'x' || characterStringInput
                    .charAt(characterStringInputIndex + 1) == 'X'));

    if (hex)
      characterStringInputIndex += 2;  // Skip over hex introducer

    /* Now collect decimal or hex digits */
    while ((hex ? isxdigit(characterStringInput
            .charAt(characterStringInputIndex)) : isdigit(characterStringInput
            .charAt(characterStringInputIndex))))
      characterStringInputIndex++;

    switch (characterStringInput.charAt(characterStringInputIndex)) {
      case 'U':
      case 'u':
      case 'L':
      case 'l':
        characterStringInputIndex++;
        switch (characterStringInput.charAt(characterStringInputIndex)) {
          case 'U':
          case 'u':
          case 'L':
          case 'l':
            characterStringInputIndex++;
          default:
            break;
        }
      default:
        break;
    }

    return characterStringInputIndex - startIndex;
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.rhul.csle.gll.GLLSupport#artBuiltin_REAL(int)
   */
  @Override
  protected int artBuiltin_REAL(int characterStringInputIndex) {
    int startIndex = characterStringInputIndex;

    if (!(isdigit(characterStringInput.charAt(characterStringInputIndex)) || characterStringInput
            .charAt(characterStringInputIndex) == '.'))  // Reals must
                                                        // contain at least
                                                        // one leading digit
      return characterStringInputIndex - startIndex;

    while (isdigit(characterStringInput.charAt(characterStringInputIndex)))
      characterStringInputIndex++;

    if (characterStringInput.charAt(characterStringInputIndex) != '.') {
      switch (characterStringInput.charAt(characterStringInputIndex)) {
        case 'e':
        case 'E':
          break;
        // An integer is a real if it has a suffix
        case 'F':
        case 'f':
        case 'D':
        case 'd':
        case 'M':
        case 'm':
          characterStringInputIndex++;
        default:
          return 0;
      }
    } else {
      characterStringInputIndex++; // skip .
      if (!(isdigit(characterStringInput.charAt(characterStringInputIndex))))
        return 0;
    }

    while (isdigit(characterStringInput.charAt(characterStringInputIndex)))
      characterStringInputIndex++;

    if (characterStringInput.charAt(characterStringInputIndex) == 'e'
            || characterStringInput.charAt(characterStringInputIndex) == 'E') {
      characterStringInputIndex++;

      if (characterStringInput.charAt(characterStringInputIndex) == '+'
              || characterStringInput.charAt(characterStringInputIndex) == '-')
        characterStringInputIndex++;

      while (isdigit(characterStringInput.charAt(characterStringInputIndex)))
        characterStringInputIndex++;
    }

    switch (characterStringInput.charAt(characterStringInputIndex)) {
      case 'F':
      case 'f':
      case 'D':
      case 'd':
      case 'M':
      case 'm':
        characterStringInputIndex++;
      default:
        return characterStringInputIndex - startIndex;
    }
  }
}
