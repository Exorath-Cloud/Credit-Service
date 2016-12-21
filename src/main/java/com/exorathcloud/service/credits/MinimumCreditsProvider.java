package com.exorathcloud.service.credits;

/**
 * Created by toonsev on 12/19/2016.
 */
public interface MinimumCreditsProvider {
    long getMinimumCredits(String userId);

    /**
     * Gets a simple {@link MinimumCreditsProvider} that returns the provided {@param minimum}
     * @param minimum
     * @return
     */
    static MinimumCreditsProvider getSimpleProvider(long minimum){
        return (userId) -> minimum;
    }
}
