package io.hhplus.tdd.util

import com.fasterxml.jackson.databind.ObjectMapper
import io.hhplus.tdd.point.service.PointService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc

abstract class CommonControllerTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @MockBean
    protected lateinit var pointService: PointService

    protected  val objectMapper = ObjectMapper()
}