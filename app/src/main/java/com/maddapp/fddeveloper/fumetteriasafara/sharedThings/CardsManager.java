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
    private static Map<String,Map<String, MembershipCard>> cardsList = new HashMap<>();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();

    /**
     * adds a listener to collect the membership cards associated with the given TCG
     * @param game the TCG to collect the membership cards from
     */
    public static void setList(String game){
        final String tempGame = game;
        if(!cardsList.containsKey(game)) {
            cardsList.put(game, new HashMap<String, MembershipCard>());
            String query = String.format("%s/Tessere",game);
            reference.child(query).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot card : dataSnapshot.getChildren()) {
                        cardsList.get(tempGame).put(card.getKey(),card.getValue(MembershipCard.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    /**
     * tryes to return the Membership card associated with the game and cardId given
     * @param game card's TCG
     * @param cardId card's id
     * @return Membership card if the card's found, null if not
     */
    public static MembershipCard getMembershipCard(String game, String cardId){
        if(cardsList.get(game) == null)
            return  null;
        return cardsList.get(game).get(cardId);
    }

    /**
     * checks if the membership card for a game and cardId couple is available
     * @param game card's TCG
     * @param cardId card's id
     * @return
     */
    public static boolean containsKey(String game, String cardId){
        return cardsList.containsKey(game) && cardsList.get(game).containsKey(cardId);
    }
}
