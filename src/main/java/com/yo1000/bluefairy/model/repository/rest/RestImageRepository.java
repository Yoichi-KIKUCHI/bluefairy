package com.yo1000.bluefairy.model.repository.rest;

import com.yo1000.bluefairy.model.entity.docker.Image;
import com.yo1000.bluefairy.model.entity.docker.ImageInspect;
import com.yo1000.bluefairy.model.repository.ImageRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by yoichi.kikuchi on 15/03/12.
 */
@Repository
public class RestImageRepository extends AbstractRestDockerRepository
        implements ImageRepository {
    @Override
    public Image[] getJson() {
        return this.getJson(false);
    }

    @Override
    public Image[] getJson(boolean all) {
        return this.getRestTemplate().getForObject(
                this.makeDockerRemoteApiEndpoint("images/json?all={all}"),
                Image[].class, all);
    }

    @Override
    public ImageInspect getInspect(String id) {
        return this.getRestTemplate().getForObject(
                this.makeDockerRemoteApiEndpoint("images/{id}/json"),
                ImageInspect.class,
                id
        );
    }
}