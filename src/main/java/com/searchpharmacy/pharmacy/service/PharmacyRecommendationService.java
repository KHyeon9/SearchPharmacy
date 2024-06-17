package com.searchpharmacy.pharmacy.service;

import com.searchpharmacy.api.dto.DocumentDto;
import com.searchpharmacy.api.dto.KakaoApiResponseDto;
import com.searchpharmacy.api.service.KakaoAddressSearchService;
import com.searchpharmacy.direaction.dto.OutputDto;
import com.searchpharmacy.direaction.entity.Direction;
import com.searchpharmacy.direaction.service.DirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PharmacyRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;

    private static final String ROAD_VIEW_BASE_URL = "https://map.kakao.com/link/roadview/";
    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";

    public List<OutputDto> recommendPharmacyList(String address) {
        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(address);

        if (Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(kakaoApiResponseDto.getDocumentList())) {
            log.error("[PharmacyRecommendationService recommendPharmacyList fail] Input address: {}", address);
            return Collections.emptyList();
        }

        DocumentDto documentDto = kakaoApiResponseDto.getDocumentList().get(0);

        // 공공 기관 약국 데이터를 및 거리 계산 알고리즘 이용
        List<Direction> directionList = directionService.buildDirectionList(documentDto);

        // 카카오 카테고리를 이용한 장소 검색 api를 이용
        // List<Direction> directionList = directionService.buildDirectionListByCategoryApi(documentDto);

        return directionService.saveAll(directionList)
                .stream()
                .map(this::convertToOutputDto)
                .collect(Collectors.toList());
    }

    private OutputDto convertToOutputDto(Direction direction) {
        String params = String.join(
                ",",
                        direction.getTargetPharmacyName(),
                        String.valueOf(direction.getTargetLatitude()),
                        String.valueOf(direction.getTargetLongitude())
                    );

        String result = UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + params).toUriString();

        log.info("direction params: {}, url: {}" ,params ,result);

        return OutputDto.builder()
                .pharmacyName(direction.getTargetPharmacyName())
                .pharmacyAddress(direction.getTargetAddress())
                .directionUrl(result)
                .roadViewUrl(ROAD_VIEW_BASE_URL + direction.getTargetLatitude() + "," + direction.getTargetLongitude())
                .distance(String.format("%.2f km", direction.getDistance()))
                .build();
    }
}
