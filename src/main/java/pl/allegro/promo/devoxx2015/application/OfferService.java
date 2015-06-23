package pl.allegro.promo.devoxx2015.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import pl.allegro.promo.devoxx2015.domain.Offer;
import pl.allegro.promo.devoxx2015.domain.OfferRepository;
import pl.allegro.promo.devoxx2015.domain.PhotoScoreSource;

@Component
public class OfferService {

    private final OfferRepository offerRepository;
    private final PhotoScoreSource photoScoreSource;
    private final double PRETTY_THRESHOLD = 0.7;

    @Autowired
    public OfferService(OfferRepository offerRepository, PhotoScoreSource photoScoreSource) {
        this.offerRepository = offerRepository;
        this.photoScoreSource = photoScoreSource;
    }

    public void processOffers(List<OfferPublishedEvent> events) {
    	double photoRate;
    	
    	for(OfferPublishedEvent event : events){
    		
    		try{
    			photoRate = photoScoreSource.getScore(event.getPhotoUrl());
    		}catch(HttpServerErrorException e){
    			photoRate = PRETTY_THRESHOLD;
    		}
    		
    		if(photoRate >= PRETTY_THRESHOLD){
    			Offer offer = new  Offer(event.getId(), event.getTitle(), event.getPhotoUrl(), photoRate);
    			offerRepository.save(offer);
    		}
    		
    	}
    }

    public List<Offer> getOffers() {
        return offerRepository.findAll(new Sort(Sort.Direction.DESC, "photoScore")); 
    }
}
