package com.pace.sfl.Utils;

import com.pace.sfl.Constants;
import com.pace.sfl.domain.ZawodnikZuzlowy;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 1/31/13
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    public static String emailPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";

    public static boolean isEmail(String email) {
        Pattern p = Pattern.compile(emailPattern); // Set the email pattern string
        Matcher m = p.matcher(email); // Match the given string with the pattern
        return m.matches();
    }

    public static double getTotalKSM(HashSet<ZawodnikZuzlowy> zawodnicy)
    {
        Iterator<ZawodnikZuzlowy> zawodnicyIterator = zawodnicy.iterator();

        double totalKSM = 0.0;
        while(zawodnicyIterator.hasNext())
        {
            totalKSM += zawodnicyIterator.next().getKsm();
        }

        return totalKSM;
    }


    public static boolean isValidBasedOnKSM(HashSet<ZawodnikZuzlowy> zawodnicy)
    {
        List<Double> ksms = new ArrayList<Double>(Constants.MAX_TEAM_PLAYERS);

        Iterator<ZawodnikZuzlowy> zawodnicyIterator = zawodnicy.iterator();

        while(zawodnicyIterator.hasNext())
        {
            ksms.add(zawodnicyIterator.next().getKsm());
        }

        Collections.sort(ksms);

        double bottom6Ksms = 0.0;
        for(int i=0; i<6; i++)
        {
            bottom6Ksms += ksms.get(i);
        }

        System.out.println("bottom 6 KSMs: "+bottom6Ksms);
        if(bottom6Ksms > 40.0)
            return false;

        return true;
    }

    public static int howManyJuniors(HashSet<ZawodnikZuzlowy> zawodnicy)
    {
        Iterator<ZawodnikZuzlowy> zawodnicyIterator = zawodnicy.iterator();

        int nrJuniors = 0;
        while(zawodnicyIterator.hasNext())
        {
            nrJuniors += zawodnicyIterator.next().isIsJunior() ? 1:0;
        }

        return nrJuniors;
    }


    public static int howManyPolish(HashSet<ZawodnikZuzlowy> zawodnicy)
    {
        Iterator<ZawodnikZuzlowy> zawodnicyIterator = zawodnicy.iterator();

        int nrPolish = 0;
        while(zawodnicyIterator.hasNext())
        {
            nrPolish += zawodnicyIterator.next().isIsPolish() ? 1:0;
        }

        return nrPolish;
    }
}
