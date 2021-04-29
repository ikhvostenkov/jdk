// This file was generated AUTOMATICALLY from a template file Wed Apr 28 22:44:43 UTC 2021
/*
 * Copyright (c) 2003, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.lang;

/** The CharacterData class encapsulates the large tables once found in
 *  java.lang.Character. 
 */

class CharacterData01 extends CharacterData {
    /* The character properties are currently encoded into 32 bits in the following manner:
        1 bit   mirrored property
        4 bits  directionality property
        9 bits  signed offset used for converting case
        1 bit   if 1, adding the signed offset converts the character to lowercase
        1 bit   if 1, subtracting the signed offset converts the character to uppercase
        1 bit   if 1, this character has a titlecase equivalent (possibly itself)
        3 bits  0  may not be part of an identifier
                1  ignorable control; may continue a Unicode identifier or Java identifier
                2  may continue a Java identifier but not a Unicode identifier (unused)
                3  may continue a Unicode identifier or Java identifier
                4  is a Java whitespace character
                5  may start or continue a Java identifier;
                   may continue but not start a Unicode identifier (underscores)
                6  may start or continue a Java identifier but not a Unicode identifier ($)
                7  may start or continue a Unicode identifier or Java identifier
                Thus:
                   5, 6, 7 may start a Java identifier
                   1, 2, 3, 5, 6, 7 may continue a Java identifier
                   7 may start a Unicode identifier
                   1, 3, 5, 7 may continue a Unicode identifier
                   1 is ignorable within an identifier
                   4 is Java whitespace
        2 bits  0  this character has no numeric property
                1  adding the digit offset to the character code and then
                   masking with 0x1F will produce the desired numeric value
                2  this character has a "strange" numeric value
                3  a Java supradecimal digit: adding the digit offset to the
                   character code, then masking with 0x1F, then adding 10
                   will produce the desired numeric value
        5 bits  digit offset
        5 bits  character type

        The encoding of character properties is subject to change at any time.
     */

    int getProperties(int ch) {
        char offset = (char)ch;
        int props = A[(Y[(X[offset>>5]<<4)|((offset>>1)&0xF)]<<1)|(offset&0x1)];
        return props;
    }

    int getPropertiesEx(int ch) {
        char offset = (char)ch;
        int props = B[(Y[(X[offset>>5]<<4)|((offset>>1)&0xF)]<<1)|(offset&0x1)];
        return props;
    }

    int getType(int ch) {
        int props = getProperties(ch);
        return (props & 0x1F);
    }

    boolean isOtherLowercase(int ch) {
        int props = getPropertiesEx(ch);
        return (props & 0x0001) != 0;
    }

    boolean isOtherUppercase(int ch) {
        int props = getPropertiesEx(ch);
        return (props & 0x0002) != 0;
    }
 
    boolean isOtherAlphabetic(int ch) {
        int props = getPropertiesEx(ch);
        return (props & 0x0004) != 0;
    }

    boolean isIdeographic(int ch) {
        int props = getPropertiesEx(ch);
        return (props & 0x0010) != 0;
    }

    boolean isJavaIdentifierStart(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00007000) >= 0x00005000);
    }

    boolean isJavaIdentifierPart(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00003000) != 0);
    }

    boolean isUnicodeIdentifierStart(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00007000) == 0x00007000);
    }

    boolean isUnicodeIdentifierPart(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00001000) != 0);
    }

    boolean isIdentifierIgnorable(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00007000) == 0x00001000);
    }

    int toLowerCase(int ch) {
        int mapChar = ch;
        int val = getProperties(ch);

        if ((val & 0x00020000) != 0) {
            int offset = val << 5 >> (5+18);
            mapChar = ch + offset;
        }
        return  mapChar;
    }

    int toUpperCase(int ch) {
        int mapChar = ch;
        int val = getProperties(ch);

        if ((val & 0x00010000) != 0) {
            int offset = val  << 5 >> (5+18);
            mapChar =  ch - offset;
        }
        return  mapChar;
    }

    int toTitleCase(int ch) {
        int mapChar = ch;
        int val = getProperties(ch);

        if ((val & 0x00008000) != 0) {
            // There is a titlecase equivalent.  Perform further checks:
            if ((val & 0x00010000) == 0) {
                // The character does not have an uppercase equivalent, so it must
                // already be uppercase; so add 1 to get the titlecase form.
                mapChar = ch + 1;
            }
            else if ((val & 0x00020000) == 0) {
                // The character does not have a lowercase equivalent, so it must
                // already be lowercase; so subtract 1 to get the titlecase form.
                mapChar = ch - 1;
            }
            // else {
            // The character has both an uppercase equivalent and a lowercase
            // equivalent, so it must itself be a titlecase form; return it.
            // return ch;
            //}
        }
        else if ((val & 0x00010000) != 0) {
            // This character has no titlecase equivalent but it does have an
            // uppercase equivalent, so use that (subtract the signed case offset).
            mapChar = toUpperCase(ch);
        }
        return  mapChar;
    }

    int digit(int ch, int radix) {
        int value = -1;
        if (radix >= Character.MIN_RADIX && radix <= Character.MAX_RADIX) {
            int val = getProperties(ch);
            int kind = val & 0x1F;
            if (kind == Character.DECIMAL_DIGIT_NUMBER) {
                value = ch + ((val & 0x3E0) >> 5) & 0x1F;
            }
            else if ((val & 0xC00) == 0x00000C00) {
                // Java supradecimal digit
                value = (ch + ((val & 0x3E0) >> 5) & 0x1F) + 10;
            }
        }
        return (value < radix) ? value : -1;
    }

    int getNumericValue(int ch) {
        int val = getProperties(ch);
        int retval = -1;

        switch (val & 0xC00) {
        default: // cannot occur
        case (0x00000000):         // not numeric
            retval = -1;
            break;
        case (0x00000400):              // simple numeric
            retval = ch + ((val & 0x3E0) >> 5) & 0x1F;
            break;
        case (0x00000800)      :       // "strange" numeric
            switch(ch) {
            case 0x10113: retval = 40; break;      // AEGEAN NUMBER FORTY
            case 0x10114: retval = 50; break;      // AEGEAN NUMBER FIFTY
            case 0x10115: retval = 60; break;      // AEGEAN NUMBER SIXTY
            case 0x10116: retval = 70; break;      // AEGEAN NUMBER SEVENTY
            case 0x10117: retval = 80; break;      // AEGEAN NUMBER EIGHTY
            case 0x10118: retval = 90; break;      // AEGEAN NUMBER NINETY
            case 0x10119: retval = 100; break;     // AEGEAN NUMBER ONE HUNDRED
            case 0x1011A: retval = 200; break;     // AEGEAN NUMBER TWO HUNDRED
            case 0x1011B: retval = 300; break;     // AEGEAN NUMBER THREE HUNDRED
            case 0x1011C: retval = 400; break;     // AEGEAN NUMBER FOUR HUNDRED
            case 0x1011D: retval = 500; break;     // AEGEAN NUMBER FIVE HUNDRED
            case 0x1011E: retval = 600; break;     // AEGEAN NUMBER SIX HUNDRED
            case 0x1011F: retval = 700; break;     // AEGEAN NUMBER SEVEN HUNDRED
            case 0x10120: retval = 800; break;     // AEGEAN NUMBER EIGHT HUNDRED
            case 0x10121: retval = 900; break;     // AEGEAN NUMBER NINE HUNDRED
            case 0x10122: retval = 1000; break;    // AEGEAN NUMBER ONE THOUSAND
            case 0x10123: retval = 2000; break;    // AEGEAN NUMBER TWO THOUSAND
            case 0x10124: retval = 3000; break;    // AEGEAN NUMBER THREE THOUSAND
            case 0x10125: retval = 4000; break;    // AEGEAN NUMBER FOUR THOUSAND
            case 0x10126: retval = 5000; break;    // AEGEAN NUMBER FIVE THOUSAND
            case 0x10127: retval = 6000; break;    // AEGEAN NUMBER SIX THOUSAND
            case 0x10128: retval = 7000; break;    // AEGEAN NUMBER SEVEN THOUSAND
            case 0x10129: retval = 8000; break;    // AEGEAN NUMBER EIGHT THOUSAND
            case 0x1012A: retval = 9000; break;    // AEGEAN NUMBER NINE THOUSAND
            case 0x1012B: retval = 10000; break;   // AEGEAN NUMBER TEN THOUSAND
            case 0x1012C: retval = 20000; break;   // AEGEAN NUMBER TWENTY THOUSAND
            case 0x1012D: retval = 30000; break;   // AEGEAN NUMBER THIRTY THOUSAND
            case 0x1012E: retval = 40000; break;   // AEGEAN NUMBER FORTY THOUSAND
            case 0x1012F: retval = 50000; break;   // AEGEAN NUMBER FIFTY THOUSAND
            case 0x10130: retval = 60000; break;   // AEGEAN NUMBER SIXTY THOUSAND
            case 0x10131: retval = 70000; break;   // AEGEAN NUMBER SEVENTY THOUSAND
            case 0x10132: retval = 80000; break;   // AEGEAN NUMBER EIGHTY THOUSAND
            case 0x10133: retval = 90000; break;   // AEGEAN NUMBER NINETY THOUSAND
            case 0x10323: retval = 50; break;      // OLD ITALIC NUMERAL FIFTY
            case 0x10144: retval = 50; break;      // ACROPHONIC ATTIC FIFTY
            case 0x10145: retval = 500; break;     // ACROPHONIC ATTIC FIVE HUNDRED
            case 0x10146: retval = 5000; break;    // ACROPHONIC ATTIC FIVE THOUSAND
            case 0x10147: retval = 50000; break;   // ACROPHONIC ATTIC FIFTY THOUSAND
            case 0x1014A: retval = 50; break;      // ACROPHONIC ATTIC FIFTY TALENTS
            case 0x1014B: retval = 100; break;     // ACROPHONIC ATTIC ONE HUNDRED TALENTS
            case 0x1014C: retval = 500; break;     // ACROPHONIC ATTIC FIVE HUNDRED TALENTS
            case 0x1014D: retval = 1000; break;    // ACROPHONIC ATTIC ONE THOUSAND TALENTS
            case 0x1014E: retval = 5000; break;    // ACROPHONIC ATTIC FIVE THOUSAND TALENTS
            case 0x10151: retval = 50; break;      // ACROPHONIC ATTIC FIFTY STATERS
            case 0x10152: retval = 100; break;     // ACROPHONIC ATTIC ONE HUNDRED STATERS
            case 0x10153: retval = 500; break;     // ACROPHONIC ATTIC FIVE HUNDRED STATERS
            case 0x10154: retval = 1000; break;    // ACROPHONIC ATTIC ONE THOUSAND STATERS
            case 0x10155: retval = 10000; break;   // ACROPHONIC ATTIC TEN THOUSAND STATERS
            case 0x10156: retval = 50000; break;   // ACROPHONIC ATTIC FIFTY THOUSAND STATERS
            case 0x10166: retval = 50; break;      // ACROPHONIC TROEZENIAN FIFTY
            case 0x10167: retval = 50; break;      // ACROPHONIC TROEZENIAN FIFTY ALTERNATE FORM
            case 0x10168: retval = 50; break;      // ACROPHONIC HERMIONIAN FIFTY
            case 0x10169: retval = 50; break;      // ACROPHONIC THESPIAN FIFTY
            case 0x1016A: retval = 100; break;     // ACROPHONIC THESPIAN ONE HUNDRED
            case 0x1016B: retval = 300; break;     // ACROPHONIC THESPIAN THREE HUNDRED
            case 0x1016C: retval = 500; break;     // ACROPHONIC EPIDAUREAN FIVE HUNDRED
            case 0x1016D: retval = 500; break;     // ACROPHONIC TROEZENIAN FIVE HUNDRED
            case 0x1016E: retval = 500; break;     // ACROPHONIC THESPIAN FIVE HUNDRED
            case 0x1016F: retval = 500; break;     // ACROPHONIC CARYSTIAN FIVE HUNDRED
            case 0x10170: retval = 500; break;     // ACROPHONIC NAXIAN FIVE HUNDRED
            case 0x10171: retval = 1000; break;    // ACROPHONIC THESPIAN ONE THOUSAND
            case 0x10172: retval = 5000; break;    // ACROPHONIC THESPIAN FIVE THOUSAND
            case 0x10174: retval = 50; break;      // ACROPHONIC STRATIAN FIFTY MNAS
            case 0x102ED: retval = 40; break;      // COPTIC EPACT NUMBER FORTY
            case 0x102EE: retval = 50; break;      // COPTIC EPACT NUMBER FIFTY
            case 0x102EF: retval = 60; break;      // COPTIC EPACT NUMBER SIXTY
            case 0x102F0: retval = 70; break;      // COPTIC EPACT NUMBER SEVENTY
            case 0x102F1: retval = 80; break;      // COPTIC EPACT NUMBER EIGHTY
            case 0x102F2: retval = 90; break;      // COPTIC EPACT NUMBER NINETY
            case 0x102F3: retval = 100; break;     // COPTIC EPACT NUMBER ONE HUNDRED
            case 0x102F4: retval = 200; break;     // COPTIC EPACT NUMBER TWO HUNDRED
            case 0x102F5: retval = 300; break;     // COPTIC EPACT NUMBER THREE HUNDRED
            case 0x102F6: retval = 400; break;     // COPTIC EPACT NUMBER FOUR HUNDRED
            case 0x102F7: retval = 500; break;     // COPTIC EPACT NUMBER FIVE HUNDRED
            case 0x102F8: retval = 600; break;     // COPTIC EPACT NUMBER SIX HUNDRED
            case 0x102F9: retval = 700; break;     // COPTIC EPACT NUMBER SEVEN HUNDRED
            case 0x102FA: retval = 800; break;     // COPTIC EPACT NUMBER EIGHT HUNDRED
            case 0x102FB: retval = 900; break;     // COPTIC EPACT NUMBER NINE HUNDRED
            case 0x10341: retval = 90; break;      // GOTHIC LETTER NINETY
            case 0x1034A: retval = 900; break;     // GOTHIC LETTER NINE HUNDRED
            case 0x103D5: retval = 100; break;     // OLD PERSIAN NUMBER HUNDRED
            case 0x1085D: retval = 100; break;     // IMPERIAL ARAMAIC NUMBER ONE HUNDRED
            case 0x1085E: retval = 1000; break;    // IMPERIAL ARAMAIC NUMBER ONE THOUSAND
            case 0x1085F: retval = 10000; break;   // IMPERIAL ARAMAIC NUMBER TEN THOUSAND
            case 0x108AF: retval = 100; break;     // NABATAEAN NUMBER ONE HUNDRED
            case 0x10919: retval = 100; break;     // PHOENICIAN NUMBER ONE HUNDRED
            case 0x10A46: retval = 100; break;     // KHAROSHTHI NUMBER ONE HUNDRED
            case 0x10A47: retval = 1000; break;    // KHAROSHTHI NUMBER ONE THOUSAND
            case 0x10A7E: retval = 50; break;      // OLD SOUTH ARABIAN NUMBER FIFTY
            case 0x10AEF: retval = 100; break;     // MANICHAEAN NUMBER ONE HUNDRED
            case 0x10B5E: retval = 100; break;     // INSCRIPTIONAL PARTHIAN NUMBER ONE HUNDRED
            case 0x10B5F: retval = 1000; break;    // INSCRIPTIONAL PARTHIAN NUMBER ONE THOUSAND
            case 0x10B7E: retval = 100; break;     // INSCRIPTIONAL PAHLAVI NUMBER ONE HUNDRED
            case 0x10B7F: retval = 1000; break;    // INSCRIPTIONAL PAHLAVI NUMBER ONE THOUSAND
            case 0x10BAF: retval = 100; break;     // PSALTER PAHLAVI NUMBER ONE HUNDRED
            case 0x10E6C: retval = 40; break;      // RUMI NUMBER FORTY
            case 0x10E6D: retval = 50; break;      // RUMI NUMBER FIFTY
            case 0x10E6E: retval = 60; break;      // RUMI NUMBER SIXTY
            case 0x10E6F: retval = 70; break;      // RUMI NUMBER SEVENTY
            case 0x10E70: retval = 80; break;      // RUMI NUMBER EIGHTY
            case 0x10E71: retval = 90; break;      // RUMI NUMBER NINETY
            case 0x10E72: retval = 100; break;     // RUMI NUMBER ONE HUNDRED
            case 0x10E73: retval = 200; break;     // RUMI NUMBER TWO HUNDRED
            case 0x10E74: retval = 300; break;     // RUMI NUMBER THREE HUNDRED
            case 0x10E75: retval = 400; break;     // RUMI NUMBER FOUR HUNDRED
            case 0x10E76: retval = 500; break;     // RUMI NUMBER FIVE HUNDRED
            case 0x10E77: retval = 600; break;     // RUMI NUMBER SIX HUNDRED
            case 0x10E78: retval = 700; break;     // RUMI NUMBER SEVEN HUNDRED
            case 0x10E79: retval = 800; break;     // RUMI NUMBER EIGHT HUNDRED
            case 0x10E7A: retval = 900; break;     // RUMI NUMBER NINE HUNDRED
            case 0x1105E: retval = 40; break;      // BRAHMI NUMBER FORTY
            case 0x1105F: retval = 50; break;      // BRAHMI NUMBER FIFTY
            case 0x11060: retval = 60; break;      // BRAHMI NUMBER SIXTY
            case 0x11061: retval = 70; break;      // BRAHMI NUMBER SEVENTY
            case 0x11062: retval = 80; break;      // BRAHMI NUMBER EIGHTY
            case 0x11063: retval = 90; break;      // BRAHMI NUMBER NINETY
            case 0x11064: retval = 100; break;     // BRAHMI NUMBER ONE HUNDRED
            case 0x11065: retval = 1000; break;    // BRAHMI NUMBER ONE THOUSAND
            case 0x111ED: retval = 40; break;      // SINHALA ARCHAIC NUMBER FORTY
            case 0x111EE: retval = 50; break;      // SINHALA ARCHAIC NUMBER FIFTY
            case 0x111EF: retval = 60; break;      // SINHALA ARCHAIC NUMBER SIXTY
            case 0x111F0: retval = 70; break;      // SINHALA ARCHAIC NUMBER SEVENTY
            case 0x111F1: retval = 80; break;      // SINHALA ARCHAIC NUMBER EIGHTY
            case 0x111F2: retval = 90; break;      // SINHALA ARCHAIC NUMBER NINETY
            case 0x111F3: retval = 100; break;     // SINHALA ARCHAIC NUMBER ONE HUNDRED
            case 0x111F4: retval = 1000; break;    // SINHALA ARCHAIC NUMBER ONE THOUSAND
            case 0x118ED: retval = 40; break;      // WARANG CITI NUMBER FORTY
            case 0x118EE: retval = 50; break;      // WARANG CITI NUMBER FIFTY
            case 0x118EF: retval = 60; break;      // WARANG CITI NUMBER SIXTY
            case 0x118F0: retval = 70; break;      // WARANG CITI NUMBER SEVENTY
            case 0x118F1: retval = 80; break;      // WARANG CITI NUMBER EIGHTY
            case 0x118F2: retval = 90; break;      // WARANG CITI NUMBER NINETY
            case 0x12432: retval = 216000; break;  // CUNEIFORM NUMERIC SIGN SHAR2 TIMES GAL PLUS DISH
            case 0x12433: retval = 432000; break;  // CUNEIFORM NUMERIC SIGN SHAR2 TIMES GAL PLUS MIN
            case 0x12467: retval = 40; break;      // CUNEIFORM NUMERIC SIGN ELAMITE FORTY
            case 0x12468: retval = 50; break;      // CUNEIFORM NUMERIC SIGN ELAMITE FIFTY
            case 0x16B5C: retval = 100; break;     // PAHAWH HMONG NUMBER HUNDREDS
            case 0x16B5D: retval = 10000; break;   // PAHAWH HMONG NUMBER TEN THOUSANDS
            case 0x16B5E: retval = 1000000; break; // PAHAWH HMONG NUMBER MILLIONS
            case 0x16B5F: retval = 100000000; break;// PAHAWH HMONG NUMBER HUNDRED MILLIONS
            case 0x1D36C: retval = 40; break;      // COUNTING ROD TENS DIGIT FOUR
            case 0x1D36D: retval = 50; break;      // COUNTING ROD TENS DIGIT FIVE
            case 0x1D36E: retval = 60; break;      // COUNTING ROD TENS DIGIT SIX
            case 0x1D36F: retval = 70; break;      // COUNTING ROD TENS DIGIT SEVEN
            case 0x1D370: retval = 80; break;      // COUNTING ROD TENS DIGIT EIGHT
            case 0x1D371: retval = 90; break;      // COUNTING ROD TENS DIGIT NINE
            default: retval = -2; break;
            }
            
            break;
        case (0x00000C00):           // Java supradecimal
            retval = (ch + ((val & 0x3E0) >> 5) & 0x1F) + 10;
            break;
        }
        return retval;
    }

    boolean isWhitespace(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00007000) == 0x00004000);
    }

    byte getDirectionality(int ch) {
        int val = getProperties(ch);
        byte directionality = (byte)((val & 0x78000000) >> 27);
        if (directionality == 0xF ) {
            directionality = Character.DIRECTIONALITY_UNDEFINED;
        }
        return directionality;
    }

    boolean isMirrored(int ch) {
        int props = getProperties(ch);
        return ((props & 0x80000000) != 0);
    }

    static final CharacterData instance = new CharacterData01();
    private CharacterData01() {};

    // The following tables and code generated using:
  // java GenerateCharacter -plane 1 -template /home/ec2-user/jdk/jdk/make/data/characterdata/CharacterData01.java.template -spec /home/ec2-user/jdk/jdk/make/data/unicodedata/UnicodeData.txt -specialcasing /home/ec2-user/jdk/jdk/make/data/unicodedata/SpecialCasing.txt -proplist /home/ec2-user/jdk/jdk/make/data/unicodedata/PropList.txt -o /home/ec2-user/jdk/build/linux-x86_64-normal-core-release/support/gensrc/java.base/java/lang/CharacterData01.java -string -usecharforbyte 11 4 1
  // The X table has 2048 entries for a total of 4096 bytes.

  static final char X[] = (
    "\000\001\002\003\004\004\004\005\006\007\010\011\012\013\014\015\003\003\003"+
    "\003\016\004\017\020\004\021\022\023\024\004\025\003\026\027\030\004\031\032"+
    "\003\003\004\033\004\034\003\003\003\003\004\004\004\004\004\004\004\004\004"+
    "\035\036\037\003\003\003\003\040\041\042\043\044\045\003\003\046\047\003\003"+
    "\050\051\003\003\052\053\054\055\056\003\057\060\050\061\062\063\064\065\003"+
    "\003\050\050\066\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\067\003\003\003\003\003\003\003\003\003\003\003\003\070\071\072\073\074"+
    "\075\076\077\100\101\102\103\104\105\106\107\110\111\003\003\003\112\113\114"+
    "\115\116\117\120\003\003\003\003\003\003\003\003\004\121\122\003\003\003\003"+
    "\003\004\123\124\003\004\125\126\003\004\127\032\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\130\131\132\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\004\133\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\004\004\004\004\004\004\004\004\004\004"+
    "\004\004\004\004\004\004\004\004\004\004\004\004\004\004\004\004\004\004\133"+
    "\003\003\003\134\135\136\137\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\004\004\004\004\004\004\004\004\004\004\004\004\004\004\004"+
    "\004\004\004\004\004\004\004\004\004\004\004\004\004\004\004\004\004\004\140"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\004\004\004\004"+
    "\004\004\004\004\004\004\004\004\004\004\004\004\004\133\141\142\003\003\112"+
    "\143\004\144\145\146\147\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\004\004\150\151\152"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\153\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\004\004\004\154\155\156\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\157\157\157\157\157\157\157\160"+
    "\157\161\157\162\163\164\165\003\166\166\167\003\003\003\003\003\166\166\170"+
    "\171\003\003\003\003\172\173\174\175\176\177\200\201\202\203\204\205\206\172"+
    "\173\207\175\210\211\212\201\213\214\215\216\217\220\221\222\223\224\225\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\050\050\050\050\050\050"+
    "\226\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\227\230\231\232\233\234\003\235\003\003\003\003\003\003\003"+
    "\003\166\236\166\166\237\240\241\242\243\244\245\246\247\003\003\250\251\252"+
    "\253\003\003\003\003\003\166\254\166\255\166\166\256\257\166\166\166\166\166"+
    "\166\166\260\166\166\261\262\166\263\166\166\166\166\264\166\166\166\265\266"+
    "\166\166\166\237\166\166\267\003\236\166\270\166\271\272\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003").toCharArray();

  // The Y table has 2992 entries for a total of 5984 bytes.

  static final char Y[] = (
    "\000\000\000\000\000\000\001\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\002\000\000\000\000\000\000\000\000\000\002\000\001\000\000\000\000\000\000"+
    "\000\003\000\000\000\000\000\000\000\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\002\003"+
    "\003\004\005\003\006\007\007\007\007\010\011\012\012\012\012\012\012\012\012"+
    "\012\012\012\012\012\012\012\012\003\013\014\014\014\014\015\016\015\015\017"+
    "\015\015\020\021\015\015\022\023\024\025\026\027\030\031\015\015\015\015\015"+
    "\015\032\033\034\035\036\036\036\036\036\036\036\036\037\040\003\036\036\036"+
    "\036\036\036\003\003\040\003\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\003\014\014\014\014\014\014\014\014\014"+
    "\014\014\014\014\014\014\014\014\014\014\014\014\014\041\003\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\002\003\000\000\000\000\000\000\000"+
    "\000\002\003\003\003\003\003\003\003\042\043\043\043\043\044\045\046\046\046"+
    "\046\046\046\046\003\003\047\050\003\003\003\003\003\003\000\000\000\000\000"+
    "\000\000\000\051\000\000\000\000\052\003\003\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\053\053\054\003\003\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\055\000\000\003\003\000\000"+
    "\000\000\056\057\060\003\003\003\003\003\061\061\061\061\061\061\061\061\061"+
    "\061\061\061\061\061\061\061\061\061\061\061\062\062\062\062\062\062\062\062"+
    "\062\062\062\062\062\062\062\062\062\062\062\062\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\003\063\063"+
    "\063\063\063\003\003\003\003\003\003\003\003\003\003\003\000\000\000\000\003"+
    "\003\003\003\000\000\000\000\000\000\000\000\000\000\003\003\003\003\003\055"+
    "\003\003\003\003\003\003\003\003\000\000\000\000\000\000\000\000\000\000\000"+
    "\002\003\003\003\003\000\000\000\000\000\000\000\000\000\000\000\003\003\003"+
    "\003\003\000\000\000\000\003\003\003\003\003\003\003\003\003\003\003\003\064"+
    "\064\064\003\065\064\064\064\064\064\064\064\064\064\064\064\064\064\064\064"+
    "\064\064\064\064\064\064\064\066\065\003\065\066\064\064\064\064\064\064\064"+
    "\064\064\064\064\067\070\071\072\073\064\064\064\064\064\064\064\064\064\064"+
    "\064\074\075\076\076\077\064\064\064\064\064\064\064\064\064\064\064\064\064"+
    "\064\064\065\003\003\003\100\101\102\103\104\003\003\003\003\003\003\003\003"+
    "\064\064\064\064\064\064\064\064\064\064\064\105\106\076\003\107\064\064\064"+
    "\064\064\064\064\064\064\064\064\064\064\003\003\067\064\064\064\064\064\064"+
    "\064\064\064\064\064\064\064\064\064\064\064\064\064\064\064\064\064\064\064"+
    "\064\064\064\003\003\003\064\110\053\111\054\003\003\053\053\064\064\066\064"+
    "\066\064\064\064\064\064\064\064\064\064\064\064\064\064\003\003\112\113\003"+
    "\114\115\115\116\073\003\003\003\003\117\117\117\117\120\003\003\003\064\064"+
    "\064\064\064\064\064\064\064\064\064\064\064\064\121\122\064\064\064\064\064"+
    "\064\064\064\064\064\064\064\064\064\121\077\064\064\064\064\123\064\064\064"+
    "\064\064\064\064\064\064\064\064\064\064\124\113\003\125\103\104\117\117\117"+
    "\120\003\003\003\003\064\064\064\064\064\064\064\064\064\064\064\003\107\126"+
    "\126\126\064\064\064\064\064\064\064\064\064\064\064\003\070\070\127\073\064"+
    "\064\064\064\064\064\064\064\064\065\003\003\070\070\127\073\064\064\064\064"+
    "\064\064\064\064\064\003\003\003\067\117\120\003\003\003\003\003\130\131\132"+
    "\104\003\003\003\003\003\003\003\003\064\064\064\064\065\003\003\003\003\003"+
    "\003\003\003\003\003\003\133\133\133\133\133\134\135\135\135\135\135\135\135"+
    "\135\135\136\137\140\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\053\053\053\053\053\053\053"+
    "\141\142\142\142\003\003\143\143\143\143\143\144\034\034\034\034\145\145\145"+
    "\145\145\003\003\003\003\003\003\003\114\112\140\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\146\137\053\147"+
    "\150\141\151\142\142\003\003\003\003\003\003\003\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\002\003\003\003\152\152\152\152\152\003\003\003\053\153"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\154\053"+
    "\053\137\053\053\155\113\156\156\156\156\156\142\142\003\003\003\003\003\003"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\157\142"+
    "\002\003\003\003\003\053\140\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\160\146\053\053\053\053\147\161"+
    "\000\162\142\005\003\055\003\152\152\152\152\152\002\003\003\163\164\164\164"+
    "\164\165\166\012\012\012\167\003\003\003\003\003\000\000\000\000\000\000\000"+
    "\000\000\001\000\000\000\000\000\000\000\000\000\000\000\000\146\137\053\146"+
    "\170\171\142\142\142\003\003\003\003\003\003\003\003\003\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\154"+
    "\146\137\053\053\155\113\003\003\152\152\152\152\152\003\003\003\111\146\001"+
    "\000\000\000\002\001\002\001\000\000\000\000\000\000\000\000\000\000\002\000"+
    "\000\000\002\000\001\000\000\003\172\146\147\146\173\174\173\174\175\003\003"+
    "\003\003\174\003\003\001\000\000\146\003\112\112\112\113\003\112\112\113\003"+
    "\003\003\003\003\000\000\000\000\000\000\000\000\146\137\053\053\147\147\146"+
    "\137\147\112\000\176\003\003\003\003\152\152\152\152\152\003\003\003\000\000"+
    "\000\000\000\000\000\160\146\053\053\003\146\146\053\150\141\142\142\142\142"+
    "\003\003\003\003\003\003\003\003\003\003\003\000\000\000\000\000\000\000\000"+
    "\146\137\053\053\053\147\137\150\177\142\002\003\003\003\003\003\152\152\152"+
    "\152\152\003\003\003\000\000\000\000\000\154\137\146\053\053\053\200\003\003"+
    "\003\003\201\201\201\201\201\201\201\201\201\201\201\201\201\201\201\201\202"+
    "\202\202\202\202\202\202\202\202\202\202\202\202\202\202\202\063\063\063\063"+
    "\063\165\166\012\012\167\003\003\003\003\003\001\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\002\003\003\003\203\203\203\203\204\204\204\205\206\206"+
    "\207\210\210\210\210\211\211\212\213\214\214\214\206\215\216\217\220\221\210"+
    "\222\223\224\225\226\227\230\231\232\232\233\234\235\236\210\237\217\217\217"+
    "\217\217\217\217\240\204\204\241\142\142\005\003\003\003\003\003\000\000\000"+
    "\000\000\000\000\002\003\003\003\003\003\003\003\003\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\002\063\063\063\063\063\003\003\142\003"+
    "\003\003\003\003\003\003\003\000\000\000\000\000\000\000\003\112\112\141\003"+
    "\003\003\003\003\000\000\000\000\000\000\000\000\053\053\053\177\142\142\014"+
    "\014\242\242\243\003\003\003\003\003\152\152\152\152\152\244\012\012\012\001"+
    "\000\000\000\000\000\000\000\000\000\000\003\003\001\000\000\000\000\000\000"+
    "\000\000\000\003\003\003\003\003\003\003\003\000\000\002\003\003\003\003\003"+
    "\160\146\146\146\146\146\146\146\146\146\146\146\146\146\146\146\146\146\146"+
    "\146\146\146\146\173\003\003\003\003\003\003\003\114\112\245\242\242\242\242"+
    "\242\242\000\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\000"+
    "\000\000\000\000\002\003\003\000\000\000\000\000\000\002\003\000\000\000\000"+
    "\002\003\003\003\000\000\000\000\000\003\041\177\246\246\003\003\003\003\003"+
    "\003\003\003\003\003\003\003\003\003\014\014\014\014\014\014\014\014\014\014"+
    "\014\014\014\014\014\014\014\014\014\014\014\014\014\014\014\014\014\003\003"+
    "\003\003\003\014\014\014\247\013\014\014\014\014\014\014\014\014\014\014\014"+
    "\014\014\250\200\112\014\250\251\251\252\246\246\246\253\112\112\112\254\041"+
    "\112\112\112\014\014\014\014\014\014\014\014\014\014\014\014\014\014\014\112"+
    "\112\014\014\014\014\014\014\014\014\014\014\014\014\014\014\014\014\014\014"+
    "\014\014\014\014\014\014\003\036\036\036\036\036\036\036\036\036\036\036\036"+
    "\036\036\036\036\036\112\255\003\003\003\003\003\003\003\003\003\003\003\003"+
    "\003\036\036\036\036\036\036\036\036\036\036\036\040\003\003\003\003\256\256"+
    "\256\256\256\257\012\012\012\003\003\003\003\003\003\003\260\260\260\260\260"+
    "\260\260\260\260\260\260\260\260\261\261\261\261\261\261\261\261\261\261\261"+
    "\261\261\260\260\260\260\260\260\260\260\260\260\260\260\260\261\261\261\262"+
    "\261\261\261\261\261\261\261\261\261\260\260\260\260\260\260\260\260\260\260"+
    "\260\260\260\261\261\261\261\261\261\261\261\261\261\261\261\261\263\260\003"+
    "\263\264\263\264\260\263\260\260\260\260\261\261\265\265\261\261\261\265\261"+
    "\261\261\261\261\260\260\260\260\260\260\260\260\260\260\260\260\260\261\261"+
    "\261\261\261\261\261\261\261\261\261\261\261\260\264\260\263\264\260\260\260"+
    "\263\260\260\260\263\261\261\261\261\261\261\261\261\261\261\261\261\261\260"+
    "\264\260\263\260\260\263\263\003\260\260\260\263\261\261\261\261\261\261\261"+
    "\261\261\261\261\261\261\260\260\260\260\260\260\260\260\260\260\260\260\260"+
    "\261\261\261\261\261\261\261\261\261\261\261\261\261\260\260\260\260\260\260"+
    "\260\261\261\261\261\261\261\261\261\261\260\261\261\261\261\261\261\261\261"+
    "\261\261\261\261\261\260\260\260\260\260\260\260\260\260\260\260\260\260\261"+
    "\261\261\261\261\261\261\261\261\261\261\261\261\260\260\260\260\260\260\260"+
    "\260\261\261\261\003\260\260\260\260\260\260\260\260\260\260\260\260\266\261"+
    "\261\261\261\261\261\261\261\261\261\261\261\267\261\261\261\260\260\260\260"+
    "\260\260\260\260\260\260\260\260\266\261\261\261\261\261\261\261\261\261\261"+
    "\261\261\267\261\261\261\260\260\260\260\260\260\260\260\260\260\260\260\266"+
    "\261\261\261\261\261\261\261\261\261\261\261\261\267\261\261\261\260\260\260"+
    "\260\260\260\260\260\260\260\260\260\266\261\261\261\261\261\261\261\261\261"+
    "\261\261\261\267\261\261\261\260\260\260\260\260\260\260\260\260\260\260\260"+
    "\266\261\261\261\261\261\261\261\261\261\261\261\261\267\261\261\261\270\003"+
    "\271\271\271\271\271\272\272\272\272\272\273\273\273\273\273\274\274\274\274"+
    "\274\275\275\275\275\275\064\064\065\100\101\101\101\101\112\112\112\113\003"+
    "\003\003\003\276\276\277\276\276\276\276\276\276\276\276\276\276\276\276\276"+
    "\277\300\300\277\277\276\276\276\276\300\276\276\277\277\003\003\003\300\003"+
    "\277\277\277\277\276\277\300\300\277\277\277\277\277\277\300\300\277\276\300"+
    "\276\276\276\300\276\276\277\276\300\300\276\276\276\276\276\277\276\276\276"+
    "\276\276\276\276\276\003\003\277\276\277\276\276\277\276\276\276\276\276\276"+
    "\276\276\003\003\003\003\003\003\003\003\003\003\301\003\003\003\003\003\003"+
    "\003\036\036\036\036\036\036\003\003\036\036\036\036\036\036\036\036\036\036"+
    "\036\036\036\036\036\036\036\036\003\003\003\003\003\003\036\036\036\036\036"+
    "\036\036\040\302\036\036\036\036\036\036\036\302\036\036\036\036\036\036\036"+
    "\302\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036"+
    "\003\003\003\003\003\303\304\304\304\304\305\306\003\014\014\014\014\014\014"+
    "\014\014\014\014\014\014\014\014\014\247\307\307\307\307\307\307\307\307\307"+
    "\307\307\307\307\014\014\014\307\307\307\307\307\307\307\307\307\307\307\307"+
    "\307\036\003\003\307\307\307\307\307\307\307\307\307\307\307\307\307\014\014"+
    "\014\014\014\014\014\014\247\003\003\003\003\003\014\014\014\014\014\014\014"+
    "\014\014\014\014\014\014\014\247\003\003\003\003\003\003\014\014\014\014\014"+
    "\014\014\014\014\014\014\014\014\014\014\014\014\014\014\014\014\247\003\003"+
    "\014\014\014\014\247\003\003\003\014\003\003\003\003\003\003\003\036\036\036"+
    "\036\036\036\040\003\036\036\036\036\036\036\036\036\036\036\036\036\036\036"+
    "\036\036\036\036\036\036\036\036\036\003\036\036\036\036\036\036\036\040\003"+
    "\003\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036"+
    "\003\003\003\003\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036"+
    "\040\036\036\036\036\036\040\003\003\036\036\036\036\036\036\036\036\036\036"+
    "\036\036\036\036\036\036\036\036\036\036\036\302\036\036\036\036\302\036\036"+
    "\036\036\036\036\036\036\036\036\036\036\036\036\040\302\036\036\036\036\036"+
    "\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036\003\003\003"+
    "\003\003\003\003\003\036\036\036\036\036\036\040\003\036\036\003\003\003\003"+
    "\003\003\036\036\036\036\036\036\036\036\036\036\040\003\003\003\003\003\036"+
    "\036\036\036\003\003\003\003\036\036\036\036\036\003\003\003\036\036\036\036"+
    "\003\003\003\003\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036"+
    "\003\003\003\003\003\003\003\003\003").toCharArray();

  // The A table has 400 entries for a total of 1600 bytes.

  static final int A[] = new int[400];
  static final String A_DATA =
    "\000\u7005\000\u7005\u7800\000\000\u7005\000\u7005\u7800\000\u7800\000\u7800"+
    "\000\000\030\u6800\030\000\030\u7800\000\u7800\000\000\u074B\000\u074B\000"+
    "\u074B\000\u074B\000\u046B\000\u058B\000\u080B\000\u080B\000\u080B\u7800\000"+
    "\000\034\000\034\000\034\u6800\u780A\u6800\u780A\u6800\u77EA\u6800\u744A\u6800"+
    "\u77AA\u6800\u742A\u6800\u780A\u6800\u76CA\u6800\u774A\u6800\u780A\u6800\u780A"+
    "\u6800\u766A\u6800\u752A\u6800\u750A\u6800\u74EA\u6800\u74EA\u6800\u74CA\u6800"+
    "\u74AA\u6800\u748A\u6800\u74CA\u6800\u754A\u6800\u752A\u6800\u750A\u6800\u74EA"+
    "\u6800\u74CA\u6800\u772A\u6800\u780A\u6800\u764A\u6800\u780A\u6800\u080B\u6800"+
    "\u080B\u6800\u080B\u6800\u080B\u6800\034\u6800\034\u6800\034\u6800\u06CB\u6800"+
    "\u080B\u6800\034\u7800\000\000\034\u4000\u3006\u4000\u3006\u1800\u040B\u1800"+
    "\u040B\u1800\u040B\u1800\u040B\u1800\u052B\u1800\u064B\u1800\u080B\u1800\u080B"+
    "\u1800\u080B\000\u042B\000\u048B\000\u050B\000\u080B\000\u7005\000\u780A\000"+
    "\u780A\u7800\000\u4000\u3006\u4000\u3006\u4000\u3006\u7800\000\u7800\000\000"+
    "\030\000\030\000\u760A\000\u760A\000\u76EA\000\u740A\000\u780A\242\u7001\242"+
    "\u7001\241\u7002\241\u7002\000\u3409\000\u3409\u0800\u7005\u0800\u7005\u0800"+
    "\u7005\u7800\000\u7800\000\u0800\u7005\u7800\000\u0800\030\u0800\u052B\u0800"+
    "\u052B\u0800\u052B\u0800\u05EB\u0800\u070B\u0800\u080B\u0800\u080B\u0800\u080B"+
    "\u0800\u7005\u0800\034\u0800\034\u0800\u050B\u0800\u050B\u0800\u050B\u0800"+
    "\u058B\u0800\u06AB\u7800\000\u0800\u074B\u0800\u074B\u0800\u074B\u0800\u074B"+
    "\u0800\u072B\u0800\u072B\u0800\u07AB\u0800\u04CB\u0800\u080B\u0800\u056B\u0800"+
    "\u066B\u0800\u078B\u0800\u080B\u7800\000\u6800\030\u0800\u7005\u4000\u3006"+
    "\u7800\000\u4000\u3006\u4000\u3006\u4000\u3006\u4000\u3006\u7800\000\u7800"+
    "\000\u4000\u3006\u0800\u042B\u0800\u042B\u0800\u04CB\u0800\u05EB\u0800\030"+
    "\u0800\030\u0800\030\u7800\000\u0800\u7005\u0800\u048B\u0800\u080B\u0800\030"+
    "\u0800\034\u0800\u7005\u0800\u7005\u4000\u3006\u7800\000\u0800\u06CB\u6800"+
    "\030\u6800\030\u0800\u05CB\u0800\u06EB\u7800\000\u0800\u070B\u0800\u070B\u0800"+
    "\u070B\u0800\u070B\u0800\u07AB\u3000\u042B\u3000\u042B\u3000\u054B\u3000\u066B"+
    "\u3000\u080B\u3000\u080B\u3000\u080B\u7800\000\000\u3008\u4000\u3006\000\u3008"+
    "\000\u7005\u4000\u3006\000\030\000\030\000\030\u6800\u05EB\u6800\u05EB\u6800"+
    "\u070B\u6800\u042B\000\u3749\000\u3749\000\u3008\000\u3008\u4000\u3006\000"+
    "\u3008\000\u3008\u4000\u3006\000\030\000\u1010\000\u3609\000\u3609\u4000\u3006"+
    "\000\u7005\000\u7005\u4000\u3006\u4000\u3006\u4000\u3006\000\u3549\000\u3549"+
    "\000\u7005\u4000\u3006\000\u7005\000\u3008\000\u3008\000\u7005\000\u7005\000"+
    "\030\u7800\000\000\u040B\000\u040B\000\u040B\000\u040B\000\u052B\000\u064B"+
    "\000\u080B\000\u080B\u7800\000\u4000\u3006\000\u3008\u4000\u3006\u4000\u3006"+
    "\u4000\u3006\000\u7005\000\u3008\u7800\000\u7800\000\000\u3008\000\u3008\000"+
    "\u3008\000\030\000\u7005\u4000\u3006\000\030\000\u3008\u4000\u3006\202\u7001"+
    "\202\u7001\201\u7002\201\u7002\000\u744A\000\u744A\000\u776A\000\u776A\000"+
    "\u776A\000\u76AA\000\u76AA\000\u76AA\000\u76AA\000\u758A\000\u758A\000\u758A"+
    "\000\u746A\000\u746A\000\u746A\000\u77EA\000\u77EA\000\u77CA\000\u77CA\000"+
    "\u77CA\000\u76AA\000\u768A\000\u768A\000\u768A\000\u780A\000\u780A\000\u75AA"+
    "\000\u75AA\000\u75AA\000\u758A\000\u752A\000\u750A\000\u750A\000\u74EA\000"+
    "\u74CA\000\u74AA\000\u74CA\000\u74CA\000\u74AA\000\u748A\000\u748A\000\u746A"+
    "\000\u746A\000\u744A\000\u742A\000\u740A\000\u770A\000\u770A\000\u770A\000"+
    "\u764A\000\u764A\000\u764A\000\u764A\000\u762A\000\u762A\000\u760A\000\u752A"+
    "\000\u752A\000\u780A\000\u776A\000\u776A\u7800\000\000\u7004\000\u7004\000"+
    "\030\000\034\u7800\000\000\u05EB\u4000\u3006\000\u7004\u4800\u1010\u4800\u1010"+
    "\000\034\u7800\000\000\034\000\u3008\000\u3008\000\u3008\000\u3008\u4800\u1010"+
    "\u4800\u1010\u4000\u3006\u4000\u3006\000\034\u4000\u3006\u6800\034\000\u042B"+
    "\000\u042B\000\u054B\000\u066B\000\u7001\000\u7001\000\u7002\000\u7002\000"+
    "\u7002\u7800\000\000\u7001\u7800\000\u7800\000\000\u7001\u7800\000\000\u7002"+
    "\000\u7001\000\031\000\u7002\uE800\031\000\u7001\000\u7002\u1800\u3649\u1800"+
    "\u3649\u1800\u3509\u1800\u3509\u1800\u37C9\u1800\u37C9\u1800\u3689\u1800\u3689"+
    "\u1800\u3549\u1800\u3549\u1000\u7005\u1000\u7005\u7800\000\u1000\u7005\u1000"+
    "\u7005\u7800\000\u6800\031\u6800\031\u7800\000\u6800\034\u1800\u040B\u1800"+
    "\u07EB\u1800\u07EB\u1800\u07EB\u1800\u07EB\u6800\u06AB\u6800\u068B\u7800\000"+
    "\000\034\000\034";

  // The B table has 400 entries for a total of 800 bytes.

  static final char B[] = (
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\004\004\004\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\004\000\004\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\004\004\004\000\000\000\000\000\000\000\000\000\000\000\004\004\004\004\004"+
    "\000\000\000\000\000\004\000\000\004\004\000\000\000\000\000\000\004\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\004\000\000\004\000\000\004"+
    "\000\000\004\004\000\000\000\004\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\006"+
    "\006").toCharArray();

  // In all, the character property tables require 11680 bytes.

    static {
                { // THIS CODE WAS AUTOMATICALLY CREATED BY GenerateCharacter:
            char[] data = A_DATA.toCharArray();
            assert (data.length == (400 * 2));
            int i = 0, j = 0;
            while (i < (400 * 2)) {
                int entry = data[i++] << 16;
                A[j++] = entry | data[i++];
            }
        }

    }        
}
