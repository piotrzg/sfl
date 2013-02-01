package com.pace.sfl.Utils;

import com.pace.sfl.Constants;
import com.pace.sfl.domain.ZawodnikZuzlowy;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 1/31/13
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

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
}
