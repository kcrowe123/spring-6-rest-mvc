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
        1 * beerService.deleteById(_)>> { args ->
            capturedArg = args[0]
        }
        beer.id == capturedArg
    }

}
