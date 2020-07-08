package org.slos.splinterlands.collection;

import org.slos.TeamSuggestionToActualCardsService;
import org.slos.splinterlands.SplinterlandsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.WeakHashMap;

@Service
public class CollectionService {
    @Autowired private SplinterlandsClient splinterlandsClient;

    private WeakHashMap<String, Collection> collectionCache = new WeakHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(TeamSuggestionToActualCardsService.class);

    public Collection getCollection(String id) {

        if (collectionCache.containsKey(id)) {
            logger.info("Returning collection from collectionCache for: " + id);
            return collectionCache.get(id);
        }
        else {
            return splinterlandsClient.getCollection(id);
        }
    }
}
