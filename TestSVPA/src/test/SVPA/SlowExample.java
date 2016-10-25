package test.SVPA;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.sat4j.specs.TimeoutException;

import com.google.common.collect.ImmutableList;

import automata.AutomataException;
import automata.svpa.Call;
import automata.svpa.Internal;
import automata.svpa.Return;
import automata.svpa.SVPA;
import automata.svpa.SVPAMove;
import theory.characters.CharPred;
import theory.characters.ICharPred;
import theory.characters.StdCharPred;
import theory.intervals.EqualitySolver;

public class SlowExample {
  
  // the '*' predicate for returns (not present in StdCharPred)
  // TODO: consider pushing into symbolicautomata library, though I'm not sure
  //       if others would make much use of this.
  private final static CharPred TRUE_RET = new CharPred(CharPred.MIN_CHAR,
                                                        CharPred.MAX_CHAR,
                                                        true);

  /**
   * allCharsExcept() builds a CharPred predicate that accepts any character
   * except for "excluded".  If "excluded" is null, it returns the TRUE
   * predicate.
   *
   * @param excluded the character to exclude
   * @param returnPred whether or not we should generate a return predicate
   * @return the predicate
   */
  private static CharPred allCharsExcept(Character excluded,
                                         boolean returnPred){
    if(excluded == null){
      if(returnPred)
        return(SlowExample.TRUE_RET);
      else
        return(StdCharPred.TRUE);
    }

    // weird stuff to avoid Java errors for increment/decrementing chars
    char prev = excluded; prev--;
    char next = excluded; next++;

    return(new CharPred(ImmutableList.of(ImmutablePair.of(CharPred.MIN_CHAR,
                                                          prev),
                                         ImmutablePair.of(next,
                                                          CharPred.MAX_CHAR)),
                                         returnPred));
  }

  public static void main(String[] args) throws TimeoutException{
    EqualitySolver ba = new EqualitySolver();

    Collection<SVPAMove<ICharPred, Character>> transitions1 = new LinkedList<SVPAMove<ICharPred, Character>>();
    transitions1.add(new Internal<ICharPred, Character>(0, 1, new CharPred((char)0x0000)));
    transitions1.add(new Internal<ICharPred, Character>(3, 2, new CharPred((char)0x0001)));
    transitions1.add(new Internal<ICharPred, Character>(5, 4, new CharPred((char)0x0003)));
    transitions1.add(new Internal<ICharPred, Character>(4, 6, new CharPred((char)0x0005)));
    transitions1.add(new Internal<ICharPred, Character>(6, 7, new CharPred((char)0x0006)));
    transitions1.add(new Internal<ICharPred, Character>(7, 3, new CharPred((char)0x0002)));
    transitions1.add(new Internal<ICharPred, Character>(9, 8, new CharPred((char)0x0007)));
    transitions1.add(new Internal<ICharPred, Character>(11, 10, new CharPred((char)0x0009)));
    transitions1.add(new Internal<ICharPred, Character>(10, 12, new CharPred((char)0x000B)));
    transitions1.add(new Internal<ICharPred, Character>(14, 13, new CharPred((char)0x000C)));
    transitions1.add(new Internal<ICharPred, Character>(15, 14, new CharPred((char)0x000D)));
    transitions1.add(new Internal<ICharPred, Character>(17, 16, new CharPred((char)0x000F)));
    transitions1.add(new Internal<ICharPred, Character>(17, 18, new CharPred((char)0x0011)));
    transitions1.add(new Internal<ICharPred, Character>(18, 19, new CharPred((char)0x0012)));
    transitions1.add(new Internal<ICharPred, Character>(21, 20, new CharPred((char)0x0013)));
    transitions1.add(new Internal<ICharPred, Character>(21, 22, new CharPred((char)0x0015)));
    transitions1.add(new Internal<ICharPred, Character>(22, 17, new CharPred((char)0x0010)));
    transitions1.add(new Internal<ICharPred, Character>(24, 23, new CharPred((char)0x0016)));
    transitions1.add(new Internal<ICharPred, Character>(23, 21, new CharPred((char)0x0014)));
    transitions1.add(new Internal<ICharPred, Character>(25, 9, new CharPred((char)0x0008)));
    transitions1.add(new Internal<ICharPred, Character>(27, 26, new CharPred((char)0x0019)));
    transitions1.add(new Internal<ICharPred, Character>(27, 28, new CharPred((char)0x001B)));
    transitions1.add(new Internal<ICharPred, Character>(30, 29, new CharPred((char)0x001C)));
    transitions1.add(new Internal<ICharPred, Character>(32, 31, new CharPred((char)0x001E)));
    transitions1.add(new Internal<ICharPred, Character>(33, 31, new CharPred((char)0x001E)));
    transitions1.add(new Internal<ICharPred, Character>(34, 32, new CharPred((char)0x001F)));
    transitions1.add(new Internal<ICharPred, Character>(34, 33, new CharPred((char)0x0020)));
    transitions1.add(new Internal<ICharPred, Character>(36, 35, new CharPred((char)0x0022)));
    transitions1.add(new Internal<ICharPred, Character>(38, 37, new CharPred((char)0x0024)));
    transitions1.add(new Internal<ICharPred, Character>(38, 36, new CharPred((char)0x0023)));
    transitions1.add(new Internal<ICharPred, Character>(28, 39, new CharPred((char)0x0026)));
    transitions1.add(new Internal<ICharPred, Character>(41, 40, new CharPred((char)0x0027)));
    transitions1.add(new Internal<ICharPred, Character>(43, 42, new CharPred((char)0x0029)));
    transitions1.add(new Internal<ICharPred, Character>(35, 43, new CharPred((char)0x002A)));
    transitions1.add(new Internal<ICharPred, Character>(35, 44, new CharPred((char)0x002B)));
    transitions1.add(new Internal<ICharPred, Character>(46, 45, new CharPred((char)0x002C)));
    transitions1.add(new Internal<ICharPred, Character>(45, 47, new CharPred((char)0x002E)));
    transitions1.add(new Internal<ICharPred, Character>(47, 48, new CharPred((char)0x002F)));
    transitions1.add(new Internal<ICharPred, Character>(48, 49, new CharPred((char)0x0030)));
    transitions1.add(new Internal<ICharPred, Character>(49, 50, new CharPred((char)0x0031)));
    transitions1.add(new Internal<ICharPred, Character>(50, 51, new CharPred((char)0x0032)));
    transitions1.add(new Internal<ICharPred, Character>(51, 52, new CharPred((char)0x0033)));
    transitions1.add(new Internal<ICharPred, Character>(52, 53, new CharPred((char)0x0034)));
    transitions1.add(new Internal<ICharPred, Character>(53, 54, new CharPred((char)0x0035)));
    transitions1.add(new Internal<ICharPred, Character>(54, 55, new CharPred((char)0x0036)));
    transitions1.add(new Internal<ICharPred, Character>(56, 34, new CharPred((char)0x0021)));
    transitions1.add(new Internal<ICharPred, Character>(56, 57, new CharPred((char)0x0038)));
    transitions1.add(new Internal<ICharPred, Character>(59, 58, new CharPred((char)0x0039)));
    transitions1.add(new Internal<ICharPred, Character>(59, 60, new CharPred((char)0x003B)));
    transitions1.add(new Internal<ICharPred, Character>(57, 61, new CharPred((char)0x003C)));
    transitions1.add(new Internal<ICharPred, Character>(62, 59, new CharPred((char)0x003A)));
    transitions1.add(new Internal<ICharPred, Character>(64, 63, new CharPred((char)0x003E)));
    transitions1.add(new Internal<ICharPred, Character>(64, 65, new CharPred((char)0x0040)));
    transitions1.add(new Internal<ICharPred, Character>(67, 66, new CharPred((char)0x0041)));
    transitions1.add(new Internal<ICharPred, Character>(60, 64, new CharPred((char)0x003F)));
    transitions1.add(new Internal<ICharPred, Character>(69, 68, new CharPred((char)0x0043)));
    transitions1.add(new Internal<ICharPred, Character>(71, 70, new CharPred((char)0x0045)));
    transitions1.add(new Internal<ICharPred, Character>(70, 67, new CharPred((char)0x0042)));
    transitions1.add(new Internal<ICharPred, Character>(65, 69, new CharPred((char)0x0044)));
    transitions1.add(new Internal<ICharPred, Character>(73, 72, new CharPred((char)0x0047)));
    transitions1.add(new Internal<ICharPred, Character>(68, 73, new CharPred((char)0x0048)));
    transitions1.add(new Internal<ICharPred, Character>(75, 74, new CharPred((char)0x0049)));
    transitions1.add(new Internal<ICharPred, Character>(77, 76, new CharPred((char)0x004B)));
    transitions1.add(new Internal<ICharPred, Character>(79, 78, new CharPred((char)0x004D)));
    transitions1.add(new Internal<ICharPred, Character>(12, 79, new CharPred((char)0x004E)));
    transitions1.add(new Internal<ICharPred, Character>(31, 80, new CharPred((char)0x004F)));
    transitions1.add(new Internal<ICharPred, Character>(81, 21, new CharPred((char)0x0014)));
    transitions1.add(new Internal<ICharPred, Character>(82, 81, new CharPred((char)0x0050)));
    transitions1.add(new Internal<ICharPred, Character>(20, 82, new CharPred((char)0x0051)));
    transitions1.add(new Internal<ICharPred, Character>(16, 24, new CharPred((char)0x0017)));
    transitions1.add(new Internal<ICharPred, Character>(83, 15, new CharPred((char)0x000E)));
    transitions1.add(new Internal<ICharPred, Character>(72, 83, new CharPred((char)0x0052)));
    transitions1.add(new Internal<ICharPred, Character>(1, 84, new CharPred((char)0x0053)));
    transitions1.add(new Internal<ICharPred, Character>(84, 82, new CharPred((char)0x0051)));
    transitions1.add(new Internal<ICharPred, Character>(84, 85, new CharPred((char)0x0054)));
    transitions1.add(new Internal<ICharPred, Character>(85, 24, new CharPred((char)0x0017)));
    transitions1.add(new Internal<ICharPred, Character>(8, 86, new CharPred((char)0x0055)));
    transitions1.add(new Internal<ICharPred, Character>(86, 87, new CharPred((char)0x0056)));
    transitions1.add(new Internal<ICharPred, Character>(89, 88, new CharPred((char)0x0057)));
    transitions1.add(new Internal<ICharPred, Character>(90, 89, new CharPred((char)0x0058)));
    transitions1.add(new Internal<ICharPred, Character>(91, 75, new CharPred((char)0x004A)));
    transitions1.add(new Internal<ICharPred, Character>(55, 91, new CharPred((char)0x005A)));
    transitions1.add(new Internal<ICharPred, Character>(74, 92, new CharPred((char)0x005B)));
    transitions1.add(new Internal<ICharPred, Character>(92, 77, new CharPred((char)0x004C)));
    transitions1.add(new Internal<ICharPred, Character>(93, 90, new CharPred((char)0x0059)));
    transitions1.add(new Internal<ICharPred, Character>(76, 93, new CharPred((char)0x005C)));
    transitions1.add(new Internal<ICharPred, Character>(58, 64, new CharPred((char)0x003F)));
    transitions1.add(new Internal<ICharPred, Character>(95, 94, new CharPred((char)0x005D)));
    transitions1.add(new Internal<ICharPred, Character>(95, 96, new CharPred((char)0x005F)));
    transitions1.add(new Internal<ICharPred, Character>(98, 97, new CharPred((char)0x0060)));
    transitions1.add(new Internal<ICharPred, Character>(98, 99, new CharPred((char)0x0062)));
    transitions1.add(new Internal<ICharPred, Character>(97, 100, new CharPred((char)0x0063)));
    transitions1.add(new Internal<ICharPred, Character>(100, 101, new CharPred((char)0x0064)));
    transitions1.add(new Internal<ICharPred, Character>(101, 73, new CharPred((char)0x0048)));
    transitions1.add(new Internal<ICharPred, Character>(99, 100, new CharPred((char)0x0063)));
    transitions1.add(new Internal<ICharPred, Character>(94, 98, new CharPred((char)0x0061)));
    transitions1.add(new Internal<ICharPred, Character>(103, 102, new CharPred((char)0x0065)));
    transitions1.add(new Internal<ICharPred, Character>(63, 69, new CharPred((char)0x0044)));
    transitions1.add(new Internal<ICharPred, Character>(105, 104, new CharPred((char)0x0067)));
    transitions1.add(new Internal<ICharPred, Character>(107, 106, new CharPred((char)0x0069)));
    transitions1.add(new Internal<ICharPred, Character>(104, 108, new CharPred((char)0x006B)));
    transitions1.add(new Internal<ICharPred, Character>(108, 109, new CharPred((char)0x006C)));
    transitions1.add(new Internal<ICharPred, Character>(109, 11, new CharPred((char)0x000A)));
    transitions1.add(new Internal<ICharPred, Character>(111, 110, new CharPred((char)0x006D)));
    transitions1.add(new Internal<ICharPred, Character>(110, 105, new CharPred((char)0x0068)));
    transitions1.add(new Internal<ICharPred, Character>(113, 112, new CharPred((char)0x006F)));
    transitions1.add(new Internal<ICharPred, Character>(114, 113, new CharPred((char)0x0070)));
    transitions1.add(new Internal<ICharPred, Character>(115, 114, new CharPred((char)0x0071)));
    transitions1.add(new Internal<ICharPred, Character>(78, 116, new CharPred((char)0x0073)));
    transitions1.add(new Internal<ICharPred, Character>(117, 1, new CharPred((char)0x0000)));
    transitions1.add(new Internal<ICharPred, Character>(118, 5, new CharPred((char)0x0004)));
    transitions1.add(new Internal<ICharPred, Character>(29, 115, new CharPred((char)0x0072)));
    transitions1.add(new Internal<ICharPred, Character>(13, 119, new CharPred((char)0x0076)));
    transitions1.add(new Internal<ICharPred, Character>(96, 98, new CharPred((char)0x0061)));
    transitions1.add(new Internal<ICharPred, Character>(19, 120, new CharPred((char)0x0077)));
    transitions1.add(new Internal<ICharPred, Character>(122, 121, new CharPred((char)0x0078)));
    transitions1.add(new Internal<ICharPred, Character>(121, 111, new CharPred((char)0x006E)));
    transitions1.add(new Internal<ICharPred, Character>(124, 123, new CharPred((char)0x007A)));
    transitions1.add(new Internal<ICharPred, Character>(123, 125, new CharPred((char)0x007C)));
    transitions1.add(new Internal<ICharPred, Character>(127, 126, new CharPred((char)0x007D)));
    transitions1.add(new Internal<ICharPred, Character>(126, 124, new CharPred((char)0x007B)));
    transitions1.add(new Internal<ICharPred, Character>(129, 128, new CharPred((char)0x007F)));
    transitions1.add(new Internal<ICharPred, Character>(128, 127, new CharPred((char)0x007E)));
    transitions1.add(new Internal<ICharPred, Character>(88, 130, new CharPred((char)0x0081)));
    transitions1.add(new Internal<ICharPred, Character>(130, 129, new CharPred((char)0x0080)));
    transitions1.add(new Internal<ICharPred, Character>(125, 131, new CharPred((char)0x0082)));
    transitions1.add(new Internal<ICharPred, Character>(131, 132, new CharPred((char)0x0083)));
    transitions1.add(new Internal<ICharPred, Character>(87, 117, new CharPred((char)0x0074)));
    transitions1.add(new Internal<ICharPred, Character>(134, 133, new CharPred((char)0x0084)));
    transitions1.add(new Internal<ICharPred, Character>(133, 107, new CharPred((char)0x006A)));
    transitions1.add(new Internal<ICharPred, Character>(136, 135, new CharPred((char)0x0086)));
    transitions1.add(new Internal<ICharPred, Character>(135, 137, new CharPred((char)0x0088)));
    transitions1.add(new Internal<ICharPred, Character>(2, 136, new CharPred((char)0x0087)));
    transitions1.add(new Internal<ICharPred, Character>(139, 138, new CharPred((char)0x0089)));
    transitions1.add(new Internal<ICharPred, Character>(138, 134, new CharPred((char)0x0085)));
    transitions1.add(new Internal<ICharPred, Character>(137, 140, new CharPred((char)0x008B)));
    transitions1.add(new Internal<ICharPred, Character>(140, 139, new CharPred((char)0x008A)));
    transitions1.add(new Internal<ICharPred, Character>(141, 122, new CharPred((char)0x0079)));
    transitions1.add(new Internal<ICharPred, Character>(142, 141, new CharPred((char)0x008C)));
    transitions1.add(new Internal<ICharPred, Character>(143, 142, new CharPred((char)0x008D)));
    transitions1.add(new Internal<ICharPred, Character>(144, 143, new CharPred((char)0x008E)));
    transitions1.add(new Internal<ICharPred, Character>(145, 144, new CharPred((char)0x008F)));
    transitions1.add(new Internal<ICharPred, Character>(146, 145, new CharPred((char)0x0090)));
    transitions1.add(new Internal<ICharPred, Character>(148, 147, new CharPred((char)0x0092)));
    transitions1.add(new Internal<ICharPred, Character>(132, 146, new CharPred((char)0x0091)));
    transitions1.add(new Internal<ICharPred, Character>(61, 103, new CharPred((char)0x0066)));
    transitions1.add(new Internal<ICharPred, Character>(61, 149, new CharPred((char)0x0094)));
    transitions1.add(new Internal<ICharPred, Character>(149, 102, new CharPred((char)0x0065)));
    transitions1.add(new Internal<ICharPred, Character>(150, 40, new CharPred((char)0x0027)));
    transitions1.add(new Internal<ICharPred, Character>(40, 56, new CharPred((char)0x0037)));
    transitions1.add(new Internal<ICharPred, Character>(26, 39, new CharPred((char)0x0026)));
    transitions1.add(new Internal<ICharPred, Character>(39, 150, new CharPred((char)0x0095)));
    transitions1.add(new Internal<ICharPred, Character>(39, 41, new CharPred((char)0x0028)));
    transitions1.add(new Internal<ICharPred, Character>(152, 151, new CharPred((char)0x0096)));
    transitions1.add(new Internal<ICharPred, Character>(153, 152, new CharPred((char)0x0097)));
    transitions1.add(new Internal<ICharPred, Character>(106, 153, new CharPred((char)0x0098)));
    transitions1.add(new Internal<ICharPred, Character>(155, 154, new CharPred((char)0x0099)));
    transitions1.add(new Internal<ICharPred, Character>(156, 155, new CharPred((char)0x009A)));
    transitions1.add(new Internal<ICharPred, Character>(157, 156, new CharPred((char)0x009B)));
    transitions1.add(new Internal<ICharPred, Character>(151, 157, new CharPred((char)0x009C)));
    transitions1.add(new Internal<ICharPred, Character>(158, 46, new CharPred((char)0x002D)));
    transitions1.add(new Internal<ICharPred, Character>(154, 158, new CharPred((char)0x009D)));
    transitions1.add(new Internal<ICharPred, Character>(160, 159, new CharPred((char)0x009E)));
    transitions1.add(new Internal<ICharPred, Character>(66, 161, new CharPred((char)0x00A0)));
    transitions1.add(new Internal<ICharPred, Character>(162, 71, new CharPred((char)0x0046)));
    transitions1.add(new Internal<ICharPred, Character>(159, 162, new CharPred((char)0x00A1)));
    transitions1.add(new Internal<ICharPred, Character>(161, 25, new CharPred((char)0x0018)));
    transitions1.add(new Internal<ICharPred, Character>(116, 160, new CharPred((char)0x009F)));
    transitions1.add(new Internal<ICharPred, Character>(102, 80, new CharPred((char)0x004F)));
    transitions1.add(new Internal<ICharPred, Character>(112, 118, new CharPred((char)0x0075)));
    transitions1.add(new Internal<ICharPred, Character>(163, 27, new CharPred((char)0x001A)));
    transitions1.add(new Internal<ICharPred, Character>(164, 163, new CharPred((char)0x00A2)));
    transitions1.add(new Internal<ICharPred, Character>(164, 38, new CharPred((char)0x0025)));
    transitions1.add(new Internal<ICharPred, Character>(165, 164, new CharPred((char)0x00A3)));
    transitions1.add(new Internal<ICharPred, Character>(147, 165, new CharPred((char)0x00A4)));
    transitions1.add(new Internal<ICharPred, Character>(166, 148, new CharPred((char)0x0093)));
    transitions1.add(new Internal<ICharPred, Character>(120, 166, new CharPred((char)0x00A5)));
    transitions1.add(new Internal<ICharPred, Character>(80, 62, new CharPred((char)0x003D)));
    transitions1.add(new Internal<ICharPred, Character>(80, 95, new CharPred((char)0x005E)));
    transitions1.add(new Internal<ICharPred, Character>(37, 35, new CharPred((char)0x0022)));
    transitions1.add(new Internal<ICharPred, Character>(42, 56, new CharPred((char)0x0037)));
    transitions1.add(new Internal<ICharPred, Character>(44, 42, new CharPred((char)0x0029)));

    // 2 goes here (it's still slow even without this one)...

    Collection<SVPAMove<ICharPred, Character>> transitions3 = new LinkedList<SVPAMove<ICharPred, Character>>();
    transitions3.add(new Internal<ICharPred, Character>(0, 0, allCharsExcept((char)0x000F, false)));
    transitions3.add(new Call<ICharPred, Character>(0, 0, 0, allCharsExcept((char)0x000F, false)));
    transitions3.add(new Return<ICharPred, Character>(0, 0, 0, allCharsExcept((char)0x000F, true)));

    Collection<SVPAMove<ICharPred, Character>> transitions4 = new LinkedList<SVPAMove<ICharPred, Character>>();
    transitions4.add(new Internal<ICharPred, Character>(0, 0, StdCharPred.TRUE));
    transitions4.add(new Call<ICharPred, Character>(0, 0, 0, StdCharPred.TRUE));
    transitions4.add(new Return<ICharPred, Character>(0, 0, 0, SlowExample.TRUE_RET));
    transitions4.add(new Internal<ICharPred, Character>(0, 1, new CharPred((char)0x0016)));
    transitions4.add(new Internal<ICharPred, Character>(1, 1, StdCharPred.TRUE));
    transitions4.add(new Call<ICharPred, Character>(1, 1, 0, StdCharPred.TRUE));
    transitions4.add(new Return<ICharPred, Character>(1, 1, 0, SlowExample.TRUE_RET));
    transitions4.add(new Internal<ICharPred, Character>(1, 2, new CharPred((char)0x0014)));
    transitions4.add(new Internal<ICharPred, Character>(2, 2, StdCharPred.TRUE));
    transitions4.add(new Call<ICharPred, Character>(2, 2, 0, StdCharPred.TRUE));
    transitions4.add(new Return<ICharPred, Character>(2, 2, 0, SlowExample.TRUE_RET));

    Collection<SVPAMove<ICharPred, Character>> transitions5 = new LinkedList<SVPAMove<ICharPred, Character>>();
    transitions5.add(new Internal<ICharPred, Character>(0, 0, StdCharPred.TRUE));
    transitions5.add(new Call<ICharPred, Character>(0, 0, 0, StdCharPred.TRUE));
    transitions5.add(new Return<ICharPred, Character>(0, 0, 0, SlowExample.TRUE_RET));
    transitions5.add(new Internal<ICharPred, Character>(0, 1, new CharPred((char)0x0050)));
    transitions5.add(new Internal<ICharPred, Character>(1, 1, StdCharPred.TRUE));
    transitions5.add(new Call<ICharPred, Character>(1, 1, 0, StdCharPred.TRUE));
    transitions5.add(new Return<ICharPred, Character>(1, 1, 0, SlowExample.TRUE_RET));
    transitions5.add(new Internal<ICharPred, Character>(1, 2, new CharPred((char)0x0014)));
    transitions5.add(new Internal<ICharPred, Character>(2, 2, StdCharPred.TRUE));
    transitions5.add(new Call<ICharPred, Character>(2, 2, 0, StdCharPred.TRUE));
    transitions5.add(new Return<ICharPred, Character>(2, 2, 0, SlowExample.TRUE_RET));

    try {
      SVPA<ICharPred, Character> svpa1 =  SVPA.MkSVPA(transitions1, Arrays.asList(0), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166), ba);
      SVPA<ICharPred, Character> svpa3 = SVPA.MkSVPA(transitions3, Arrays.asList(0), Arrays.asList(0), ba);
      SVPA<ICharPred, Character> svpa4 = SVPA.MkSVPA(transitions4, Arrays.asList(0), Arrays.asList(2), ba);
      SVPA<ICharPred, Character> svpa5 = SVPA.MkSVPA(transitions5, Arrays.asList(0), Arrays.asList(2), ba);

      SVPA<ICharPred, Character> result = svpa1.intersectionWith(svpa3, ba);
      result = result.intersectionWith(svpa4, ba);
      result = result.intersectionWith(svpa5, ba);
      System.out.println("Is it empty? " + result.isEmpty);
    } catch (AutomataException e) {
      System.err.println("Ooops something went wrong!");
    }
  }
}
