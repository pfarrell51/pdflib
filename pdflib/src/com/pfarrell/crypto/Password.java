package com.pfarrell.crypto;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.pfarrell.exceptions.UnacceptablePassphraseException;
import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 * The <code>Password</code> class provides an opaque wrapper for
 * passwords and how we obfuscate them. 
 *
 * We provide a dummy value if the input string is not acceptable.
 * We expect more serious policy to be defined in the beans and UI.
 *
 * List of bad words from http://www.whatsmypass.com/ and CERT
 *
 * @author  Pat Farrell
 * @version 0.51, 2000/03/06
 * Copyright (C) 2006-2013 Patrick Farrell. All rights reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  
 */

public class Password  implements Serializable {
/** internal storage of obfuscated password */    
private String phrase;
/** username to validate against */
private String username;
private final String salt;
/** fixed length */
private static final int PRESETlength = 32;
/** length of salt we use */
public static final int SALTlength = 4;
/** the normal alphabet */
public static final String[] ALPHABET = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
/**
 * default constructor
 */
public Password() {
    phrase = null;
    salt = makeSalt();
}
/**
 * allocate a <code>Password</code> object on the stack
 * @param arg user's username
 */
public Password(String arg) {
    Preconditions.checkNotNull(arg);
    username = arg;
    salt = makeSalt();
}
/**
 * allocate a <code>Password</code> object on the stack
 * @param uName user's username
 * @param clearText clear password
 */
public Password(String uName, String clearText) {
    Preconditions.checkNotNull(uName);
    username = uName;
    salt = makeSalt();
    phrase = obfuscate(clearText, salt);
}

private Password(String usernameArg, String phraseArg, String saltArg, boolean alreadyHashed) {
    username = usernameArg;
    phrase = alreadyHashed ? phraseArg : obfuscate(phraseArg, saltArg);
    salt = saltArg;
}
public static boolean isSame(String usernameArg, String clearText, String salt, String obfuscated) {
    Password sample = new Password(usernameArg, clearText, salt, false);
    String sampleObs = sample.getPhrase();
    return obfuscated.equals(sampleObs);
}
/**
 * create a Password object with a pre-assigned salt
 * @param usernameArg the user name
 * @param obfuscated the obfuscated result of a prior call to create a password
 * @param saltArg the salt used in prior times
 * @return  a nice Password to use
 */
public static Password makePasswordFromValues(String usernameArg, String obfuscated, String saltArg) {
    Preconditions.checkNotNull(usernameArg);
    Preconditions.checkNotNull(obfuscated);
    Password rval = new Password(usernameArg, obfuscated, saltArg, true);
    return rval;
}

private String makeSalt() {
    StringBuilder rval = new StringBuilder();
    SecRandom sRan = SecRandom.getInstance();
    for (int i = 0; i < SALTlength; i++) {
        int idx = sRan.nextInt(26);
        rval.append(ALPHABET[idx]);
    }
    return rval.toString();
}
/**
 * get password phrase
 * @return password (in nice String form)
 */
public String getPhrase() { return phrase;};
/**
 * usual toString() for convienence
 * @return human readable value
 */
    @Override
public String toString() { return getPhrase();}
/**
 * set the password/passphrase
 * @param in clear text password/phrase
 * @param saltArg the salt
 * @throws UnacceptablePassphraseException if pass phrase fails test.
 */
public void setPhraseAndSalt(String in, String saltArg) { 
    Preconditions.checkNotNull(in);
    Preconditions.checkNotNull(saltArg);
    if ( isAcceptablePassphrase(in)) {
        phrase = obfuscate(in, saltArg); 
    } else {
        phrase = null;
        if ( in != null && in.length() > 0) {
            String msg = "unacceptable pass phrase: " + in;
            Logger.getLogger(this.getClass()).error(msg);
            throw new UnacceptablePassphraseException(msg);
        }
    }
}

   /**
    * static test to see if incoming passphrase could be acceptable
 * @param username 
 * @param in incomming passphrase
    * @return true if 'in' could be a good phrase, false if not acceptable
    */
public static boolean validatePhrase(String username, String in) {
    Preconditions.checkNotNull(username);
    Preconditions.checkNotNull(in);
    Password working = new Password(username);
    boolean rval = working.isAcceptablePassphrase(in);
    return rval;
}
/**
 * push the passphrase through a one way hash
 * @param arg input string to process
 * @return hex result
 */
private String obfuscate(String arg, String salt) {
    Preconditions.checkNotNull(arg);
    String newmac = HmacUtil.hmac(seed, salt == null ? arg : arg + salt);
    String rval = newmac.substring(0,PRESETlength);
    return rval;
}

    /**
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }
/**
 * sets the pass phrase already obfuscated.
 * @param in the pass phrase already obfuscated.
     * @deprecated 
     */
@Deprecated
public void setPreobfuscatedPhrase(String in) { 
    if ( in == null) throw new RuntimeException("null value illegal");
    if ( in.length() != PRESETlength) throw new  RuntimeException("wrong length for preset phrase");
    String[] parts = in.split("\\p{XDigit}*");
    if ( parts.length  > 0) throw new RuntimeException("illegal character found");
    phrase = in;
}
/**
 * validate input against policy. Null and short are replaced.
 * @param arg incomming string
 * @return true if acceptable, false otherwise
 */
private boolean isAcceptablePassphrase(String arg) {
    if (arg == null || arg.length() < 6) return false;
    String lowerArg = arg.toLowerCase().trim();
    if (lowerArg.equalsIgnoreCase(username)) {
        return false;
    }
    if (badWords.contains(lowerArg)) {
        return false;
    }
    if ( lowerArg.startsWith("xxx")) {
        int xcount = 0;
        for (int i = 0; i < arg.length(); i++) {
            char c = arg.charAt(i);
            if ( c == 'x' || c == 'X') {
                xcount++;
            }
        }
        if ( xcount+3 > arg.length()) return false;
    }
    return true;
}
/** fast acccess set of bad words */
public final static ImmutableSet<String> badWords;
static {
    ImmutableSet.Builder<String> builder = new ImmutableSet.Builder<String>();
  // (your first name)
   // userid

    builder.add("0").add("000000").add("00000000").add("007").add("1");
    builder.add("110").add("111").add("111111").add("11111111").add("112233");
    builder.add("12").add("121212").add("123").add("123123").add("1234");
    builder.add("12345").add("123456").add("1234567").add("12345678").add("123456789");
    builder.add("1234qwer").add("123abc").add("123asd").add("123qwe").add("131313");
    builder.add("2002").add("2003").add("232323").add("2600").add("54321");
    builder.add("654321").add("666666").add("696969").add("777777").add("7777777");
    builder.add("8675309").add("88888888").add("987654").add("a").add("aaa");
    builder.add("aaaaaa").add("abc").add("abc123").add("abcd").add("abcdef");
    builder.add("abgrtyu").add("access").add("access14").add("action").add("admin");
    builder.add("admin123").add("administrator").add("albert").add("alexis").add("alpha");
    builder.add("amanda").add("amateur").add("andrea").add("andrew").add("angela");
    builder.add("angels").add("animal").add("anthony").add("apollo").add("apples");
    builder.add("arsenal").add("arthur").add("asdf").add("asdfgh").add("ashley");
    builder.add("asshole").add("august").add("austin").add("badboy").add("bailey");
    builder.add("banana").add("barney").add("baseball").add("batman").add("beaver");
    builder.add("beavis").add("bigcock").add("bigdaddy").add("bigdick").add("bigdog");
    builder.add("bigtits").add("birdie").add("bitches").add("biteme").add("blazer");
    builder.add("blonde").add("blondes").add("blowjob").add("blowme").add("bond007");
    builder.add("bonnie").add("booboo").add("booger").add("boomer").add("boston");
    builder.add("brandon").add("brandy").add("braves").add("brazil").add("bronco");
    builder.add("broncos").add("bulldog").add("buster").add("butter").add("butthead");
    builder.add("calvin").add("camaro").add("cameron").add("canada").add("captain");
    builder.add("carlos").add("carter").add("casper").add("charles").add("charlie");
    builder.add("cheese").add("chelsea").add("chester").add("chicago").add("chicken");
    builder.add("cocacola").add("coffee").add("college").add("compaq").add("computer");
    builder.add("cookie").add("cooper").add("corvette").add("cowboy").add("cowboys");
    builder.add("crystal").add("cumming").add("cumshot").add("dakota").add("dallas");
    builder.add("daniel").add("danielle").add("database").add("debbie").add("dennis");
    builder.add("diablo").add("diamond").add("doctor").add("doggie").add("dolphin");
    builder.add("dolphins").add("donald").add("dragon").add("dreams").add("driver");
    builder.add("eagle1").add("eagles").add("edward").add("einstein").add("enable");
    builder.add("erotic").add("extreme").add("falcon").add("fender").add("ferrari");
    builder.add("firebird").add("fishing").add("florida").add("flower").add("flyers");
    builder.add("foobar").add("football").add("forever").add("freddy").add("freedom");
    builder.add("fucked").add("fucker").add("fucking").add("fuckme").add("fuckyou");
    builder.add("gandalf").add("gateway").add("gators").add("gemini").add("george");
    builder.add("giants").add("ginger").add("god").add("godblessyou").add("golden");
    builder.add("golfer").add("gordon").add("gregory").add("guitar").add("gunner");
    builder.add("hammer").add("hannah").add("hardcore").add("harley").add("heather");
    builder.add("helpme").add("hentai").add("hockey").add("home").add("hooters");
    builder.add("horney").add("hotdog").add("hunter").add("hunting").add("iceman");
    builder.add("ihavenopass").add("iloveyou").add("internet").add("iwantu").add("jackie");
    builder.add("jackson").add("jaguar").add("jasmine").add("jasper").add("jennifer");
    builder.add("jeremy").add("jessica").add("johnny").add("johnson").add("jordan");
    builder.add("joseph").add("joshua").add("junior").add("justin").add("killer");
    builder.add("knight").add("ladies").add("lakers").add("lauren").add("leather");
    builder.add("legend").add("letmein").add("link182").add("little").add("login");
    builder.add("london").add("love").add("lovers").add("maddog").add("madison");
    builder.add("maggie").add("magnum").add("marine").add("marlboro").add("martin");
    builder.add("marvin").add("master").add("matrix").add("matthew").add("maverick");
    builder.add("maxwell").add("melissa").add("member").add("mercedes").add("merlin");
    builder.add("michael").add("michelle").add("mickey").add("midnight").add("miller");
    builder.add("mistress").add("monica").add("monkey").add("monster").add("morgan");
    builder.add("mother").add("mountain").add("muffin").add("murphy").add("mustang");
    builder.add("mypass").add("mypass123").add("mypc").add("mypc123").add("myspace1");
    builder.add("naked").add("nascar").add("nathan").add("naughty").add("ncc1701");
    builder.add("newyork").add("nicholas").add("nicole").add("nipple").add("nipples");
    builder.add("oliver").add("oracle").add("orange").add("owner").add("packers");
    builder.add("panther").add("panties").add("parker").add("pass").add("passwd");
    builder.add("password").add("password1").add("password12").add("password123").add("pat");
    builder.add("patrick").add("pc").add("peaches").add("peanut").add("pepper");
    builder.add("phantom").add("phoenix").add("player").add("please").add("pookie");
    builder.add("porsche").add("prince").add("princess").add("private").add("purple");
    builder.add("pussies").add("pw").add("pw123").add("pwd").add("qazwsx");
    builder.add("qwer").add("qwerty").add("qwertyui").add("rabbit").add("rachel");
    builder.add("racing").add("raiders").add("rainbow").add("ranger").add("rangers");
    builder.add("rebecca").add("redskins").add("redsox").add("redwings").add("richard");
    builder.add("robert").add("rocket").add("root").add("rosebud").add("runner");
    builder.add("rush2112").add("russia").add("samantha").add("sammy").add("samson");
    builder.add("sandra").add("saturn").add("scooby").add("scooter").add("scorpio");
    builder.add("scorpion").add("secret").add("seinfeld").add("server").add("sex").add("sexsex");   // Seinfeld added due to Sony hack
    builder.add("shadow").add("shannon").add("shaved").add("sierra").add("silver");
    builder.add("skippy").add("slayer").add("smokey").add("snoopy").add("soccer");
    builder.add("sophie").add("spanky").add("sparky").add("spider").add("squirt");
    builder.add("srinivas").add("startrek").add("starwars").add("steelers").add("steven");
    builder.add("sticky").add("stupid").add("success").add("suckit").add("summer");
    builder.add("sunshine").add("super").add("superman").add("surfer").add("swimming");
    builder.add("sybase").add("sydney").add("taylor").add("temp").add("temp123");
    builder.add("tennis").add("teresa").add("test").add("test123").add("tester");
    builder.add("testing").add("theman").add("thomas").add("thunder").add("thx1138");
    builder.add("tiffany").add("tigers").add("tigger").add("tomcat").add("topgun");
    builder.add("toyota").add("travis").add("trouble").add("trustno1").add("tucker");
    builder.add("turtle").add("twitter").add("united").add("vagina").add("victor");
    builder.add("victoria").add("viking").add("voodoo").add("voyager").add("walter");
    builder.add("warrior").add("welcome").add("whatever").add("william").add("willie");
    builder.add("wilson").add("win").add("winner").add("winston").add("winter");
    builder.add("wizard").add("xavier").add("xp").add("xxxxxx").add("xxxxxxxx");
    builder.add("yamaha").add("yankee").add("yankees").add("yellow").add("yxcv");
    builder.add("zxcv").add("zxcvbn").add("zxcvbnm").add("zzzzzz");

    badWords = builder.build();
}

/** the secret key, never let anyone see this */
private static String seed = "squeamish ossifragezUM+UVSlIYCBIMS3C3OfOpE/PfP4vd8BFiOBAZoaTvra" +
    "OBCDTH869zqH3eehxzYR3m818rNV2s3Sr3f+8l8xO6BRkNTV/jjzQsXNhUkWE";


}

