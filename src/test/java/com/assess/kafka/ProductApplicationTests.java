package com.assess.kafka;

import com.assess.kafka.consumer.consumer.ProductEventsConsumerManualOffset;
import com.assess.kafka.producer.controller.ProductController;
import com.assess.kafka.producer.domain.ProductDto;
import com.assess.kafka.producer.domain.ProductEvent;
import com.assess.kafka.producer.domain.ProductEventType;
import com.assess.kafka.producer.producer.ProductEventProducer;
import com.assess.kafka.testutils.MasterData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static com.assess.kafka.testutils.TestUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;


@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@EnableKafka
public class ProductApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private KafkaTemplate<String, ProductEvent> kafkaTemplate;


    @MockBean
    private ProductEventProducer productEventProducer;

    @MockBean
    private ProductEventsConsumerManualOffset productEventsConsumerManualOffset;

    @Test
    public void test_BookControllerSendBook() throws Exception {
        final int[] count = new int[1];
        ProductDto productDto = ProductDto.builder()
                .productDate(LocalDateTime.now())
                .customerId(1L)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(5000L))
                .build();
        ProductEvent productEvent = ProductEvent.
                builder()
                .eventDetails("Create Product")
                .eventType(ProductEventType.PRODUCT_ALLOTED)
                .productDto(productDto)
                .build();

        when(productEventProducer.sendCreateProductEvent(productDto, "Product Created")).then(new Answer<ProductEvent>() {

            @Override
            public ProductEvent answer(InvocationOnMock invocation) throws Throwable {
                // TODO Auto-generated method stub
                count[0]++;
                return productEvent;
            }
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/products/")
                .content(MasterData.asJsonString(productDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        yakshaAssert(currentTest(), count[0] == 1, businessTestFile);

    }

    @Test
    public void testSendBook() throws Exception {
        ProductDto productDto = ProductDto.builder()
                .productDate(LocalDateTime.now())
                .customerId(1L)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(5000L))
                .build();
        ProductEvent productEvent = ProductEvent.
                builder()
                .eventDetails("Create Product")
                .eventType(ProductEventType.PRODUCT_ALLOTED)
                .productDto(productDto)
                .build();
        try {
            CompletableFuture<SendResult<String, ProductEvent>> mockFuture = mock(CompletableFuture.class);
            when(kafkaTemplate.send("create-product", productEvent.getEventType().toString(), productEvent)).thenReturn(mockFuture);
            this.productEventProducer.sendCreateProductEvent(productDto, "Product Created");
            yakshaAssert(currentTest(), true, businessTestFile);
        } catch (Exception ex) {
            yakshaAssert(currentTest(), false, businessTestFile);
        }

    }

    @Test
    @Disabled
    public void testConsumeBook() {
        ProductDto productDto = ProductDto.builder()
                .productDate(LocalDateTime.now())
                .customerId(1L)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(5000L))
                .build();
        ProductEvent productEvent = ProductEvent.
                builder()
                .eventDetails("Create Product")
                .eventType(ProductEventType.PRODUCT_ALLOTED)
                .productDto(productDto)
                .build();

        kafkaTemplate.send("create-product", productEvent.getEventType().toString(), productEvent);


        await().atMost(5, SECONDS).untilAsserted(() -> {
            ConsumerRecord<String, ProductEvent> mockRecord = mock(ConsumerRecord.class);

            Acknowledgment mockAcknowledgment = mock(Acknowledgment.class);
            productEventsConsumerManualOffset.onMessage(mockRecord, mockAcknowledgment);
            yakshaAssert(currentTest(), true, businessTestFile);

        });
    }


}
