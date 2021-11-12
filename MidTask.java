package com.mycompany.midlab;

import java.util.*;
import java.io.*;

public class MidTask {

    static String Grammar[][], FIRST_SET[], FOLLOW_SET[], Rules[][], Rules_FIRST_SET[], ParsingTable[][];
    static char nonterm[], term[], termsarr[];
    static int NTlen, Tlen, nRules;
    static int index;

    public static void main(String args[]) throws IOException {
        System.out.println("\n |=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|  TAKING INPUTS  |=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=| \n");
        String NT, T;
        int u, v, w;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\n ------------------           Enter Non-terminals");
        NT = br.readLine();
        NTlen = NT.length();
        nonterm = new char[NTlen];
        nonterm = NT.toCharArray();// creating an array of non-terminals 

        System.out.println(" ------------------           Enter Terminals");
        T = br.readLine();
        Tlen = T.length();
        term = new char[Tlen];
        term = T.toCharArray();// creating an array of terminals

        System.out.println("\n ------------------           Specify Grammar                      (Enter 8 for epsilon rule ( epsilon = 8 )) \n");

        Grammar = new String[NTlen][];// initializing grammars 2d array (no of rows set equal to (non-terminals length))
        ParsingTable = new String[NTlen + 1][Tlen + 2]; // initializing parsing table 2d array

        // Taking # of rules for each non-terminal in grammar (u = row# , v = column = rule no#)
        nRules = 0;
        for (u = 0; u < NTlen; u++) {
            System.out.println("                              Please Enter the number of rules for  " + nonterm[u] + " ");

            w = Integer.parseInt(br.readLine());// # of rules
            Grammar[u] = new String[w];
//            System.out.println("Grammar[u] " + Grammar[u] + " " + Grammar[u].getClass().getSimpleName());

            System.out.println(" -----           Now Enter the rules");
            for (v = 0; v < w; v++) {
                Grammar[u][v] = br.readLine();
//                Rules[nRules][1]=Grammar[u][v];
                nRules += 1;
            }
        }

        Rules = new String[nRules][3]; // initializing rules 2d array

        DisplayProvidedData();
        Display_FirstSet_FollowSet();
        Display_Create_Parse_Table();
        
    }

    static String FindFirst(int i) {
//        System.out.println("\n-----------------FIND FIRST SET------------------");

        int j, k, l = 0, found = 0;
        String tempVar = "", str = "";

        for (j = 0; j < Grammar[i].length; j++) {// # of rules
//            System.out.println("            Grammar[i][j] = " + Grammar[i][j]);// Rule 

            for (k = 0; k < Grammar[i][j].length(); k++, found = 0) {

                for (l = 0; l < NTlen; l++) {
                    if (Grammar[i][j].charAt(k) == nonterm[l]) // checking if first symbol in rule is non terminal
                    {
                        str = FindFirst(l);
//                        System.out.println("            str " + str);
                        if (!(str.length() == 1 && str.charAt(0) == '8')) {
//                            tempVar = tempVar + str + " , ";
                            tempVar = tempVar + str;
                        }
                        found = 1;
                        break;
                    }
                }
                if (found == 1) {
                    if (str.contains("8")) //here epsilon will lead to next non-terminal’s first set
                    {
                        continue;
                    }
                } else //if first set includes terminal
                {
//                    tempVar = tempVar + Grammar[i][j].charAt(k) + " , ";
                    tempVar = tempVar + Grammar[i][j].charAt(k);
                }
                break;
            }
        }
        return tempVar;
    }

    static String FindRulesFirst(int i) {
        int k, l = 0, found = 0;
        String tempVar = "", str = "";

//        System.out.println("                          Rules[i] = " + Rules[i][1]);// Rule 
//        System.out.println("                          Rules[i] = " + Rules[i][1].length());// Rule length or # of Symbols
        for (k = 0; k < Rules[i][1].length(); k++, found = 0) {

            for (l = 0; l < NTlen; l++) {
//                System.out.println("                          Rules[i].charAt(k) = " + Rules[i][1].charAt(k));
                if (Rules[i][1].charAt(k) == nonterm[l]) // checking if first symbol in rule is non terminal
                {
                    str = FindFirst(l);
                    if (!(str.length() == 1 && str.charAt(0) == '8')) {
//                            tempVar = tempVar + str + " , ";
                        tempVar = tempVar + str;
//                            perRuleFirstSet = str;
                    }
                    found = 1;
                    break;
                }
            }
            if (found == 1) {
                if (str.contains("8")) //here epsilon will lead to next non-terminal’s first set
                {
                    continue;
                }
            } else //if first set includes terminal
            {
//tempVar = tempVar + Grammar[i][j].charAt(k) + " , ";
                tempVar = tempVar + Rules[i][1].charAt(k);
            }
            break;
        }
//        System.out.println("                          Rules First set tempVar " + tempVar);
        index = 0;
        if (tempVar.contains("8")) {
            for (int g = 0; g < NTlen; g++) {
//                System.out.println("nonterm[] = " + nonterm[g]);
                if (Rules[i][0].contains(Character.toString(nonterm[g]))) {
                    index = g;
                    break;
                }
            }
//            System.out.println("index = " + index);
            tempVar = FindFollow(index);
        }
        return tempVar;
    }

    static String FindFollow(int i) {
        char rule[], chr[];
        String tempVar = "";
        int j, k, l, m, n, found = 0;
        if (i == 0) {
            tempVar = "$";
        }
        for (j = 0; j < NTlen; j++) {
//            System.out.println("Grammar[j] "+j+" "+Grammar[j]);
            for (k = 0; k < Grammar[j].length; k++) {
                rule = new char[Grammar[j][k].length()];
                rule = Grammar[j][k].toCharArray();
                for (l = 0; l < rule.length; l++) {
                    if (rule[l] == nonterm[i]) {
                        if (l == rule.length - 1) {
                            if (j < i) {
                                tempVar = tempVar + FOLLOW_SET[j];
                            }
                        } else {
                            for (m = 0; m < NTlen; m++) {
                                if (rule[l + 1] == nonterm[m]) {
                                    chr = new char[FIRST_SET[m].length()];
                                    chr = FIRST_SET[m].toCharArray();
                                    for (n = 0; n < chr.length; n++) {
                                        if (chr[n] == '8') {
                                            if (l + 1 == rule.length - 1) {
                                                tempVar = tempVar + FindFollow(j);
                                            } else {
                                                tempVar = tempVar + FindFollow(m);
                                            }
                                        } else {
                                            tempVar = tempVar + chr[n];
                                        }
                                    }
                                    found = 1;
                                }
                            }
                            if (found != 1) {
                                tempVar = tempVar + rule[l + 1];
                            }
                        }
                    }
                }
            }
        }
//        System.out.println("Follow Set tempVar" + tempVar);
        return tempVar;
    }

    static String remove_Duplicates(String str) {
        int i;
        char ch;
        boolean seen[] = new boolean[256];
        StringBuilder stringb = new StringBuilder(seen.length);
        for (i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (!seen[ch]) {
                seen[ch] = true;
                stringb.append(ch);
            }
        }
        return stringb.toString();
    }

    static void DisplayProvidedData() {
        System.out.println("\n|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|  PROVIDED DATA |=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=| \n");
        System.out.println("\n              ---------------------------  Non-Terminals  ---------------------------              ");
        System.out.println("                               " + Arrays.toString(nonterm));
        System.out.println("\n              ---------------------------  Terminals  ---------------------------              ");
        System.out.println("                               " + Arrays.toString(term));

        int count = 0;
        System.out.println("\n              ---------------------------  GRAMMER  ---------------------------              ");
        for (int d = 0; d < Grammar.length; d++) {
            System.out.print("                               " + nonterm[d] + " --> ");

            for (int dd = 0; dd < Grammar[d].length; dd++) {
                if (Grammar[d][dd].contains("8")) {
                    System.out.print("epsilon");
                    Rules[count][0] = Character.toString(nonterm[d]);
                    Rules[count][1] = Grammar[d][dd];
                    count += 1;
                } else {
                    System.out.print(Grammar[d][dd]);
                    Rules[count][0] = Character.toString(nonterm[d]);
                    Rules[count][1] = Grammar[d][dd];
                    count += 1;
                }
                if (dd != Grammar[d].length - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println("");
        }
        System.out.println("\n              ---------------------------  RULES  ---------------------------              ");

        for (int i = 0; i < Rules.length; i++) {
            System.out.print("                               Rule " + (i + 1) + "  --->  ");
            for (int j = 0; j < Rules[i].length; j++) {
                System.out.print(Rules[i][j] + "      ");
            }
            System.out.println("");
        }
    }

    static void Display_FirstSet_FollowSet() {
        int u;
        System.out.println("\n|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|  FIRST SET |=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=| \n");
        FIRST_SET = new String[NTlen];// string length
//        System.out.println("FIRST_SET " + FIRST_SET + "   " + FIRST_SET.getClass().getSimpleName());
        for (u = 0; u < NTlen; u++) {
            FIRST_SET[u] = FindFirst(u);
        }
//        for (u = 0; u < NTlen; u++) {
//            System.out.println("FIRST_SET[" + u + "] " + FIRST_SET[u]);
//        }
        for (u = 0; u < NTlen; u++) {
            System.out.println("                               FIRST(" + nonterm[u] + ") = { " + remove_Duplicates(FIRST_SET[u]) + " } ");
        }

        System.out.println("\n|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|  FOLLOW SET |=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=| \n");

        FOLLOW_SET = new String[NTlen];
        for (u = 0; u < NTlen; u++) {
            FOLLOW_SET[u] = FindFollow(u);
        }
        for (u = 0; u < NTlen; u++) {
            System.out.println("                               FOLLOW(" + nonterm[u] + ") = { " + remove_Duplicates(FOLLOW_SET[u]) + " } ");
        }

//        System.out.println("\n              ---------------------------  RULE-WISE Set   ---------------------------               \n");
//        System.out.println("\n nRules = " + nRules);// 7

        Rules_FIRST_SET = new String[nRules];// string length = 7

// Assigning the Rules 2d array column 2 values where nonTerm having epsilon in First set then finding that nonTerm's Follow set tokens
        for (u = 0; u < nRules; u++) {
            Rules_FIRST_SET[u] = FindRulesFirst(u);
//            System.out.println("Rules FIRST_SET[" + u + "] " + Rules_FIRST_SET[u]);
            Rules[u][2] = Rules_FIRST_SET[u];
        }

        System.out.println("\n|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=| Parsing Table RULES |=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=| \n");

        System.out.println("                               " + Arrays.deepToString(Rules)
                .replace("],", "\n                               ").replace(",", "\t| ")
                .replaceAll("[\\[\\]]", " "));
    }

    static void Display_Create_Parse_Table() {

// Initializing Parse table
        ParsingTable[0][0] = "";
        ParsingTable[0][Tlen + 1] = "$";
        for (int p = 1; p < Tlen + 1; p++) {
            ParsingTable[0][p] = Character.toString(term[p - 1]);
        }
        for (int q = 1; q < NTlen + 1; q++) {
            ParsingTable[q][0] = Character.toString(nonterm[q - 1]);
        }
// ALL parse table terminals are placed in char array along with $ 
        char T[] = new char[term.length + 1];

        for (int i = 0; i < T.length; i++) {
            if (i < T.length - 1) {
                T[i] = term[i];
            }
            if (i == T.length - 1) {
                T[i] = '$';
            }
        }
//Parse table terminals are displayed
//        for (int i = 0; i < T.length; i++) {
//            System.out.println("T[" + i + "] = " + T[i]);
//        }

        int ruleno = 0;// can be incremented upyo 6 as we have total 7 rules
        int d = 1;
        while (d < ParsingTable.length) {// rows = NONTERMS = 5
//            System.out.print(nonterm[d - 1] + " --> ");

            for (int dd = 1; dd < ParsingTable[d].length; dd++) {// columns = TERMS = 6

                String NonTerms = Rules[ruleno][0];
                String Rrule = Rules[ruleno][1];
                String Terms = Rules[ruleno][2];
                termsarr = Terms.toCharArray();
                int TermsLen = termsarr.length;

                if (nonterm[d - 1] == NonTerms.charAt(0)) {
// T all terminals array items are traversed and matched with First & follow terms in dd loop
//                    System.out.println("T[" + (dd - 1) + "]  =   " + T[dd - 1]);
                    boolean found = false;
                    for (char c : termsarr) {
                        if (c == T[dd - 1]) {
                            found = true;
                            ParsingTable[d][dd] = Rrule;
//                            System.out.println("ParsingTable[" + d + "][" + dd + "] assigned value");
                            break;
                        }
                    }
                }
            }
            if (ruleno < 6) {
                ruleno += 1;
            } else {
                break;
            }
//            System.out.println("nonterm[" + (d - 1) + "]==Rules[ruleno][0].charAt(0) : " + nonterm[d - 1] + "     " + Rules[ruleno][0].charAt(0));
            if (nonterm[d - 1] == (Rules[ruleno][0].charAt(0))) {
            } else {
                d++;
            }
        }
// Making null values = " "
        for (int row = 0; row < ParsingTable.length; row++) {
            for (int col = 0; col < ParsingTable[row].length; col++) {
                if (ParsingTable[row][col] == null) {
                    ParsingTable[row][col] = " ";
                }
            }
        }
        System.out.println("\n |=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=| Parsing Table |=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=|=| \n");
        System.out.println("                               " + Arrays.deepToString(ParsingTable)
                .replace("],", "\n                               ").replace(",", "\t| ")
                .replaceAll("[\\[\\]]", " "));
        
        System.out.println("\n                                                                                            Here 8 = epsilon \n");
    }
}
