package com.maddapp.fddeveloper.fumetteriasafara.sharedThings;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maddapp.fddeveloper.fumetteriasafara.databaseInteractions.dbEntities.MembershipCard;


import java.util.HashMap;
import java.util.Map;

/**
 *  A membership card manager which prevents from loading multiple times the same membership cards
 */
public class CardsManager {
    private static Map<String,Map<String, MembershipCard>> listaTessere = new HashMap<>();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();

    public static void setList(String gioco){
        final String giocoTemp = gioco;
        if(!listaTessere.containsKey(gioco)) {
            listaTessere.put(gioco, new HashMap<String, MembershipCard>());
            String query = String.format("%s/Tessere",gioco);
            reference.child(query).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot tessera : dataSnapshot.getChildren()) {
                        listaTessere.get(giocoTemp).put(tessera.getKey(),tessera.getValue(MembershipCard.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TODO
                }
            });
        }
    }

    public static Map<String, MembershipCard> getTessere(String gioco){
        return listaTessere.get(gioco);
    }

    public static MembershipCard getTessera(String gioco, String id){
        if(listaTessere.get(gioco) == null)
            return  null;
        return listaTessere.get(gioco).get(id);
    }

    public static boolean containsKey(String gioco, String id){
        return listaTessere.containsKey(gioco) && listaTessere.get(gioco).containsKey(id);
    }
}
