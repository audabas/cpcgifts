package fr.cpcgifts.persistance;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import fr.cpcgifts.model.*;

public class OfyService {

	static {
        factory().register(AdminRequest.class);
        factory().register(Comment.class);
        factory().register(CpcUser.class);
        factory().register(Giveaway.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }

	
}
