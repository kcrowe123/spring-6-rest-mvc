package guru.springframework.spring6restmvc.controller


import com.fasterxml.jackson.databind.ObjectMapper
import guru.springframework.spring6restmvc.model.Beer
import guru.springframework.spring6restmvc.services.BeerService
import guru.springframework.spring6restmvc.services.BeerServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.util.HashMap
import java.util.Map
import java.util.UUID

import static org.assertj.core.api.Assertions.assertThat
import static org.hamcrest.core.Is.is
import static org.mockito.ArgumentMatchers.any
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.verify
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = BeerController.class)
class BeerControllerSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @SpringBean
    BeerService beerService = Mock()

    BeerServiceImpl beerServiceImpl

    @Autowired
    ObjectMapper objectMapper

    def setup() {
        // This runs before each test method
        beerServiceImpl = new BeerServiceImpl()
    }

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    def "GetBeerById"() {

        when:
        def result = mockMvc.perform(get("/api/v1/beer/" + UUID.randomUUID()))

        then:
        result.andExpect(status().isOk())
    }

    def "should delete beer successfully"() {
        given:
        def beer = beerServiceImpl.listBeers().get(0)
        def capturedArg = null

        when:
        def result = mockMvc.perform(delete("/api/v1/beer/${beer.id}")
                .accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isNoContent())
        1 * beerService.deleteById(_) >> { args ->
            capturedArg = args[0]
        }
        beer.id == capturedArg
    }

    void "test update beer by id"() {
        given: "a beer object and its ID"
        Beer beer = beerServiceImpl.listBeers().get(1) // Using second beer for testing
        UUID beerId = beer.getId()
        def capturedUUID = null
        def capturedBeer = null

        and: "the beer update logic is mocked"
        def beerJson = objectMapper.writeValueAsString(beer)

        when: "sending a PUT request to update a beer"
        def result = mockMvc.perform(put("/api/v1/beer/${beerId}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(beerJson))

        then: "the response status is 204 (No Content)"
        result.andExpect(status().isNoContent())

        and: "the beerService updateBeerById method is called with correct parameters"
        1 * beerService.updateBeerById(_, _) >> { args ->
            capturedUUID = args[0]
            capturedBeer = args[1]
        }

        and: "the captured UUID and beer match the one from the input"
        capturedUUID == beerId
        capturedBeer == beer
    }

}
