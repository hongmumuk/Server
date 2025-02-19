package hongmumuk.hongmumuk.service;

import hongmumuk.hongmumuk.common.response.Apiresponse;
import hongmumuk.hongmumuk.common.response.status.SuccessStatus;
import hongmumuk.hongmumuk.dto.CategoryDto;
import hongmumuk.hongmumuk.dto.SearchRestaurantDto;
import hongmumuk.hongmumuk.dto.RestaurantPageDto;
import hongmumuk.hongmumuk.entity.Category;
import hongmumuk.hongmumuk.entity.Restaurant;
import hongmumuk.hongmumuk.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final RestaurantRepository restaurantRepository;

    @Transactional
    public ResponseEntity<?> findCategory(CategoryDto categoryDto) {
        String category = categoryDto.getCategory();
        Integer page = categoryDto.getPage();
        String sort = categoryDto.getSort();

        // page가 -1이면 -> 검색 기능
        // pagination 없이 모든 식당 정보 주기
        if (page == -1) {
            List<Restaurant> restaurants = restaurantRepository.findAll(Sort.by(Sort.Direction.DESC, sort));
            List<SearchRestaurantDto> searchRestaurantDto = restaurants.stream()
                    .map(SearchRestaurantDto::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK, searchRestaurantDto));
        }
        // page가 -1이 아니면 -> 카테고리 조회 기능
        else {
            Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, sort));
            Page<RestaurantPageDto> restaurantPageDto;

            // ALL이면 -> 전체 조회
            // 모든 식당 조회해서 pagination 적용해서 넘겨주기
            if (category.equals("ALL")) {
                restaurantPageDto = restaurantRepository.findAll(pageable).map(RestaurantPageDto::from);

            }
            // ALL이 아니면 -> 카테고리별 조회
            // 카테고리에 맞는 식당 조회해서 pagination 적용해서 넘겨주기
            else{
                restaurantPageDto = restaurantRepository.findAllByCategory(Category.valueOf(category), pageable).map(RestaurantPageDto::from);
            }
            return ResponseEntity.ok(Apiresponse.isSuccess(SuccessStatus.OK, restaurantPageDto.getContent()));
        }
    }
}
