package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.*;
import edu.miu.horelo.dto.response.EstoreResponse;
import edu.miu.horelo.dto.response.EstoreDTORes;
import edu.miu.horelo.dto.response.EstoreVerticalNavBarResponse;
import edu.miu.horelo.dto.response.FoodSafetyMessageResponse;
import edu.miu.horelo.model.Estore;
import edu.miu.horelo.model.User;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface EstoreService {

    Optional<EstoreVerticalNavBarResponse> getStoreById(Long id);

    List<EstoreResponse> getStoreByOwner(String email);
    List<EstoreDTORes> getStoreByUser(int userId);

    EstoreResponse addNewStore(Integer userId, EstoreRequest estoreRequest) throws BadRequestException;
    EstoreResponse updateStore(Long id,Integer userId, EstoreFullRequest estoreFullRequest);

    Estore updateOpenHours(Long estoreId, OpenDaysAndHoursDTO openDaysAndHours);

    Estore updateEstore1(Long estoreId, EstoreDTO updates, User user);
    Estore updateEstoreAddress(Long estoreId, EstoreAddressDTO updates);
    EstoreResponse updateEstoreLogo(Long estoreId, MultipartFile file, User user);
    FoodSafetyMessageResponse updateEstoreFoodSafetyMessage(Long estoreId, FoodSafetyMessageRequest foodSafetyMessageRequest);


    Page<EstoreResponse> searchEstores(String areaCode, String street, String city, String state, int page, int size);
}
