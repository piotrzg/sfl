package com.pace.sfl.Utils;

import com.pace.sfl.Constants;
import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.ZawodnikZuzlowy;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.ui.Model;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 1/31/13
 */
public class Utils {

    public static String emailPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";

    public static boolean isEmail(String email) {
        Pattern p = Pattern.compile(emailPattern); // Set the email pattern string
        Matcher m = p.matcher(email); // Match the given string with the pattern
        return m.matches();
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
        for(int i=0; i<6 && i<zawodnicy.size(); i++)
        {
            bottom6Ksms += ksms.get(i);
        }

        if(bottom6Ksms > 40.0)
            return false;

        return true;
    }


    public static boolean meetsMinimumKSM(HashSet<ZawodnikZuzlowy> zawodnicy)
    {
        List<Double> ksms = new ArrayList<Double>(Constants.MAX_TEAM_PLAYERS);

        Iterator<ZawodnikZuzlowy> zawodnicyIterator = zawodnicy.iterator();

        while(zawodnicyIterator.hasNext())
        {
            ksms.add(zawodnicyIterator.next().getKsm());
        }

        Collections.sort(ksms, Collections.reverseOrder());

        double bottom6Ksms = 0.0;
        for(int i=0; i<6 && i<zawodnicy.size(); i++)
        {
            bottom6Ksms += ksms.get(i);
        }

        if(bottom6Ksms < 33.0)
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


    public static double parseBiegiStr(String bStr)
    {
        double totalPoints = 0.0;
        bStr = bStr.trim();
        String [] biegi  = bStr.split(",");

        Pattern p = Pattern.compile( "([0-9]*)(\\.[0|5])?");

        for(int i=0; i<biegi.length; i++)
        {
            String bieg = biegi[i];
            bieg = bieg.trim();
            Matcher m = p.matcher(bieg);
            if(m.matches())
                totalPoints += Double.valueOf(bieg);
            else
            {
                if(bieg.indexOf('*')>-1)
                {
                    String parsedBieg = bieg.substring(0,bieg.indexOf('*'));
                    m = p.matcher(parsedBieg);
                    if(m.matches())
                        totalPoints += Double.valueOf(parsedBieg);
                }
                else
                {
                    continue;
                }
            }
        }

        return totalPoints;
    }


    public static Model populateModel(Model uiModel, SflDruzyna sflDruzyna)
    {
        boolean isValidTeamBasedOnKSM = isValidBasedOnKSM((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("isValidTeamBasedOnKSM",isValidTeamBasedOnKSM);
        int nrJuniors = howManyJuniors((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("nrJuniors", nrJuniors);
        int nrPolish = howManyPolish((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("nrPolish", nrPolish);
        uiModel.addAttribute("druzyna", sflDruzyna);

        return uiModel;
    }


    /**
     * Source: http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
     */
    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );

        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

}
