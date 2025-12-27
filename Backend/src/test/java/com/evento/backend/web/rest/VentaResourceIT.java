package com.evento.backend.web.rest;

import static com.evento.backend.domain.VentaAsserts.*;
import static com.evento.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static com.evento.backend.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.evento.backend.IntegrationTest;
import com.evento.backend.domain.Evento;
import com.evento.backend.domain.User;
import com.evento.backend.domain.Venta;
import com.evento.backend.domain.enumeration.EstadoSincronizacion;
import com.evento.backend.repository.UserRepository;
import com.evento.backend.repository.VentaRepository;
import com.evento.backend.service.VentaService;
import com.evento.backend.service.dto.VentaDTO;
import com.evento.backend.service.mapper.VentaMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VentaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VentaResourceIT {

    private static final Long DEFAULT_ID_VENTA_CATEDRA = 1L;
    private static final Long UPDATED_ID_VENTA_CATEDRA = 2L;
    private static final Long SMALLER_ID_VENTA_CATEDRA = 1L - 1L;

    private static final Instant DEFAULT_FECHA_VENTA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_VENTA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_PRECIO_TOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO_TOTAL = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRECIO_TOTAL = new BigDecimal(0 - 1);

    private static final Boolean DEFAULT_EXITOSA = false;
    private static final Boolean UPDATED_EXITOSA = true;

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final EstadoSincronizacion DEFAULT_ESTADO_SINCRONIZACION = EstadoSincronizacion.PENDIENTE;
    private static final EstadoSincronizacion UPDATED_ESTADO_SINCRONIZACION = EstadoSincronizacion.SINCRONIZADA;

    private static final String ENTITY_API_URL = "/api/ventas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private VentaRepository ventaRepositoryMock;

    @Autowired
    private VentaMapper ventaMapper;

    @Mock
    private VentaService ventaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVentaMockMvc;

    private Venta venta;

    private Venta insertedVenta;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Venta createEntity(EntityManager em) {
        Venta venta = new Venta()
            .idVentaCatedra(DEFAULT_ID_VENTA_CATEDRA)
            .fechaVenta(DEFAULT_FECHA_VENTA)
            .precioTotal(DEFAULT_PRECIO_TOTAL)
            .exitosa(DEFAULT_EXITOSA)
            .descripcion(DEFAULT_DESCRIPCION)
            .estadoSincronizacion(DEFAULT_ESTADO_SINCRONIZACION);
        // Add required entity
        Evento evento;
        if (TestUtil.findAll(em, Evento.class).isEmpty()) {
            evento = EventoResourceIT.createEntity(em);
            em.persist(evento);
            em.flush();
        } else {
            evento = TestUtil.findAll(em, Evento.class).get(0);
        }
        venta.setEvento(evento);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        venta.setUsuario(user);
        return venta;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Venta createUpdatedEntity(EntityManager em) {
        Venta updatedVenta = new Venta()
            .idVentaCatedra(UPDATED_ID_VENTA_CATEDRA)
            .fechaVenta(UPDATED_FECHA_VENTA)
            .precioTotal(UPDATED_PRECIO_TOTAL)
            .exitosa(UPDATED_EXITOSA)
            .descripcion(UPDATED_DESCRIPCION)
            .estadoSincronizacion(UPDATED_ESTADO_SINCRONIZACION);
        // Add required entity
        Evento evento;
        if (TestUtil.findAll(em, Evento.class).isEmpty()) {
            evento = EventoResourceIT.createUpdatedEntity(em);
            em.persist(evento);
            em.flush();
        } else {
            evento = TestUtil.findAll(em, Evento.class).get(0);
        }
        updatedVenta.setEvento(evento);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedVenta.setUsuario(user);
        return updatedVenta;
    }

    @BeforeEach
    void initTest() {
        venta = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedVenta != null) {
            ventaRepository.delete(insertedVenta);
            insertedVenta = null;
        }
    }

    @Test
    @Transactional
    void createVenta() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);
        var returnedVentaDTO = om.readValue(
            restVentaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VentaDTO.class
        );

        // Validate the Venta in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVenta = ventaMapper.toEntity(returnedVentaDTO);
        assertVentaUpdatableFieldsEquals(returnedVenta, getPersistedVenta(returnedVenta));

        insertedVenta = returnedVenta;
    }

    @Test
    @Transactional
    void createVentaWithExistingId() throws Exception {
        // Create the Venta with an existing ID
        venta.setId(1L);
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFechaVentaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        venta.setFechaVenta(null);

        // Create the Venta, which fails.
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        restVentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrecioTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        venta.setPrecioTotal(null);

        // Create the Venta, which fails.
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        restVentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExitosaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        venta.setExitosa(null);

        // Create the Venta, which fails.
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        restVentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEstadoSincronizacionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        venta.setEstadoSincronizacion(null);

        // Create the Venta, which fails.
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        restVentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVentas() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList
        restVentaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(venta.getId().intValue())))
            .andExpect(jsonPath("$.[*].idVentaCatedra").value(hasItem(DEFAULT_ID_VENTA_CATEDRA.intValue())))
            .andExpect(jsonPath("$.[*].fechaVenta").value(hasItem(DEFAULT_FECHA_VENTA.toString())))
            .andExpect(jsonPath("$.[*].precioTotal").value(hasItem(sameNumber(DEFAULT_PRECIO_TOTAL))))
            .andExpect(jsonPath("$.[*].exitosa").value(hasItem(DEFAULT_EXITOSA)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].estadoSincronizacion").value(hasItem(DEFAULT_ESTADO_SINCRONIZACION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVentasWithEagerRelationshipsIsEnabled() throws Exception {
        when(ventaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVentaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ventaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVentasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ventaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVentaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ventaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getVenta() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get the venta
        restVentaMockMvc
            .perform(get(ENTITY_API_URL_ID, venta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(venta.getId().intValue()))
            .andExpect(jsonPath("$.idVentaCatedra").value(DEFAULT_ID_VENTA_CATEDRA.intValue()))
            .andExpect(jsonPath("$.fechaVenta").value(DEFAULT_FECHA_VENTA.toString()))
            .andExpect(jsonPath("$.precioTotal").value(sameNumber(DEFAULT_PRECIO_TOTAL)))
            .andExpect(jsonPath("$.exitosa").value(DEFAULT_EXITOSA))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.estadoSincronizacion").value(DEFAULT_ESTADO_SINCRONIZACION.toString()));
    }

    @Test
    @Transactional
    void getVentasByIdFiltering() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        Long id = venta.getId();

        defaultVentaFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultVentaFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultVentaFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVentasByIdVentaCatedraIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where idVentaCatedra equals to
        defaultVentaFiltering("idVentaCatedra.equals=" + DEFAULT_ID_VENTA_CATEDRA, "idVentaCatedra.equals=" + UPDATED_ID_VENTA_CATEDRA);
    }

    @Test
    @Transactional
    void getAllVentasByIdVentaCatedraIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where idVentaCatedra in
        defaultVentaFiltering(
            "idVentaCatedra.in=" + DEFAULT_ID_VENTA_CATEDRA + "," + UPDATED_ID_VENTA_CATEDRA,
            "idVentaCatedra.in=" + UPDATED_ID_VENTA_CATEDRA
        );
    }

    @Test
    @Transactional
    void getAllVentasByIdVentaCatedraIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where idVentaCatedra is not null
        defaultVentaFiltering("idVentaCatedra.specified=true", "idVentaCatedra.specified=false");
    }

    @Test
    @Transactional
    void getAllVentasByIdVentaCatedraIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where idVentaCatedra is greater than or equal to
        defaultVentaFiltering(
            "idVentaCatedra.greaterThanOrEqual=" + DEFAULT_ID_VENTA_CATEDRA,
            "idVentaCatedra.greaterThanOrEqual=" + UPDATED_ID_VENTA_CATEDRA
        );
    }

    @Test
    @Transactional
    void getAllVentasByIdVentaCatedraIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where idVentaCatedra is less than or equal to
        defaultVentaFiltering(
            "idVentaCatedra.lessThanOrEqual=" + DEFAULT_ID_VENTA_CATEDRA,
            "idVentaCatedra.lessThanOrEqual=" + SMALLER_ID_VENTA_CATEDRA
        );
    }

    @Test
    @Transactional
    void getAllVentasByIdVentaCatedraIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where idVentaCatedra is less than
        defaultVentaFiltering("idVentaCatedra.lessThan=" + UPDATED_ID_VENTA_CATEDRA, "idVentaCatedra.lessThan=" + DEFAULT_ID_VENTA_CATEDRA);
    }

    @Test
    @Transactional
    void getAllVentasByIdVentaCatedraIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where idVentaCatedra is greater than
        defaultVentaFiltering(
            "idVentaCatedra.greaterThan=" + SMALLER_ID_VENTA_CATEDRA,
            "idVentaCatedra.greaterThan=" + DEFAULT_ID_VENTA_CATEDRA
        );
    }

    @Test
    @Transactional
    void getAllVentasByFechaVentaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where fechaVenta equals to
        defaultVentaFiltering("fechaVenta.equals=" + DEFAULT_FECHA_VENTA, "fechaVenta.equals=" + UPDATED_FECHA_VENTA);
    }

    @Test
    @Transactional
    void getAllVentasByFechaVentaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where fechaVenta in
        defaultVentaFiltering("fechaVenta.in=" + DEFAULT_FECHA_VENTA + "," + UPDATED_FECHA_VENTA, "fechaVenta.in=" + UPDATED_FECHA_VENTA);
    }

    @Test
    @Transactional
    void getAllVentasByFechaVentaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where fechaVenta is not null
        defaultVentaFiltering("fechaVenta.specified=true", "fechaVenta.specified=false");
    }

    @Test
    @Transactional
    void getAllVentasByPrecioTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioTotal equals to
        defaultVentaFiltering("precioTotal.equals=" + DEFAULT_PRECIO_TOTAL, "precioTotal.equals=" + UPDATED_PRECIO_TOTAL);
    }

    @Test
    @Transactional
    void getAllVentasByPrecioTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioTotal in
        defaultVentaFiltering(
            "precioTotal.in=" + DEFAULT_PRECIO_TOTAL + "," + UPDATED_PRECIO_TOTAL,
            "precioTotal.in=" + UPDATED_PRECIO_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllVentasByPrecioTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioTotal is not null
        defaultVentaFiltering("precioTotal.specified=true", "precioTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllVentasByPrecioTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioTotal is greater than or equal to
        defaultVentaFiltering(
            "precioTotal.greaterThanOrEqual=" + DEFAULT_PRECIO_TOTAL,
            "precioTotal.greaterThanOrEqual=" + UPDATED_PRECIO_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllVentasByPrecioTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioTotal is less than or equal to
        defaultVentaFiltering("precioTotal.lessThanOrEqual=" + DEFAULT_PRECIO_TOTAL, "precioTotal.lessThanOrEqual=" + SMALLER_PRECIO_TOTAL);
    }

    @Test
    @Transactional
    void getAllVentasByPrecioTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioTotal is less than
        defaultVentaFiltering("precioTotal.lessThan=" + UPDATED_PRECIO_TOTAL, "precioTotal.lessThan=" + DEFAULT_PRECIO_TOTAL);
    }

    @Test
    @Transactional
    void getAllVentasByPrecioTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where precioTotal is greater than
        defaultVentaFiltering("precioTotal.greaterThan=" + SMALLER_PRECIO_TOTAL, "precioTotal.greaterThan=" + DEFAULT_PRECIO_TOTAL);
    }

    @Test
    @Transactional
    void getAllVentasByExitosaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where exitosa equals to
        defaultVentaFiltering("exitosa.equals=" + DEFAULT_EXITOSA, "exitosa.equals=" + UPDATED_EXITOSA);
    }

    @Test
    @Transactional
    void getAllVentasByExitosaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where exitosa in
        defaultVentaFiltering("exitosa.in=" + DEFAULT_EXITOSA + "," + UPDATED_EXITOSA, "exitosa.in=" + UPDATED_EXITOSA);
    }

    @Test
    @Transactional
    void getAllVentasByExitosaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where exitosa is not null
        defaultVentaFiltering("exitosa.specified=true", "exitosa.specified=false");
    }

    @Test
    @Transactional
    void getAllVentasByDescripcionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where descripcion equals to
        defaultVentaFiltering("descripcion.equals=" + DEFAULT_DESCRIPCION, "descripcion.equals=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllVentasByDescripcionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where descripcion in
        defaultVentaFiltering("descripcion.in=" + DEFAULT_DESCRIPCION + "," + UPDATED_DESCRIPCION, "descripcion.in=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllVentasByDescripcionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where descripcion is not null
        defaultVentaFiltering("descripcion.specified=true", "descripcion.specified=false");
    }

    @Test
    @Transactional
    void getAllVentasByDescripcionContainsSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where descripcion contains
        defaultVentaFiltering("descripcion.contains=" + DEFAULT_DESCRIPCION, "descripcion.contains=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllVentasByDescripcionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where descripcion does not contain
        defaultVentaFiltering("descripcion.doesNotContain=" + UPDATED_DESCRIPCION, "descripcion.doesNotContain=" + DEFAULT_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllVentasByEstadoSincronizacionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where estadoSincronizacion equals to
        defaultVentaFiltering(
            "estadoSincronizacion.equals=" + DEFAULT_ESTADO_SINCRONIZACION,
            "estadoSincronizacion.equals=" + UPDATED_ESTADO_SINCRONIZACION
        );
    }

    @Test
    @Transactional
    void getAllVentasByEstadoSincronizacionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where estadoSincronizacion in
        defaultVentaFiltering(
            "estadoSincronizacion.in=" + DEFAULT_ESTADO_SINCRONIZACION + "," + UPDATED_ESTADO_SINCRONIZACION,
            "estadoSincronizacion.in=" + UPDATED_ESTADO_SINCRONIZACION
        );
    }

    @Test
    @Transactional
    void getAllVentasByEstadoSincronizacionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        // Get all the ventaList where estadoSincronizacion is not null
        defaultVentaFiltering("estadoSincronizacion.specified=true", "estadoSincronizacion.specified=false");
    }

    @Test
    @Transactional
    void getAllVentasByEventoIsEqualToSomething() throws Exception {
        Evento evento;
        if (TestUtil.findAll(em, Evento.class).isEmpty()) {
            ventaRepository.saveAndFlush(venta);
            evento = EventoResourceIT.createEntity(em);
        } else {
            evento = TestUtil.findAll(em, Evento.class).get(0);
        }
        em.persist(evento);
        em.flush();
        venta.setEvento(evento);
        ventaRepository.saveAndFlush(venta);
        Long eventoId = evento.getId();
        // Get all the ventaList where evento equals to eventoId
        defaultVentaShouldBeFound("eventoId.equals=" + eventoId);

        // Get all the ventaList where evento equals to (eventoId + 1)
        defaultVentaShouldNotBeFound("eventoId.equals=" + (eventoId + 1));
    }

    @Test
    @Transactional
    void getAllVentasByUsuarioIsEqualToSomething() throws Exception {
        User usuario;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            ventaRepository.saveAndFlush(venta);
            usuario = UserResourceIT.createEntity();
        } else {
            usuario = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(usuario);
        em.flush();
        venta.setUsuario(usuario);
        ventaRepository.saveAndFlush(venta);
        Long usuarioId = usuario.getId();
        // Get all the ventaList where usuario equals to usuarioId
        defaultVentaShouldBeFound("usuarioId.equals=" + usuarioId);

        // Get all the ventaList where usuario equals to (usuarioId + 1)
        defaultVentaShouldNotBeFound("usuarioId.equals=" + (usuarioId + 1));
    }

    private void defaultVentaFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultVentaShouldBeFound(shouldBeFound);
        defaultVentaShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVentaShouldBeFound(String filter) throws Exception {
        restVentaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(venta.getId().intValue())))
            .andExpect(jsonPath("$.[*].idVentaCatedra").value(hasItem(DEFAULT_ID_VENTA_CATEDRA.intValue())))
            .andExpect(jsonPath("$.[*].fechaVenta").value(hasItem(DEFAULT_FECHA_VENTA.toString())))
            .andExpect(jsonPath("$.[*].precioTotal").value(hasItem(sameNumber(DEFAULT_PRECIO_TOTAL))))
            .andExpect(jsonPath("$.[*].exitosa").value(hasItem(DEFAULT_EXITOSA)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].estadoSincronizacion").value(hasItem(DEFAULT_ESTADO_SINCRONIZACION.toString())));

        // Check, that the count call also returns 1
        restVentaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVentaShouldNotBeFound(String filter) throws Exception {
        restVentaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVentaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVenta() throws Exception {
        // Get the venta
        restVentaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVenta() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the venta
        Venta updatedVenta = ventaRepository.findById(venta.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVenta are not directly saved in db
        em.detach(updatedVenta);
        updatedVenta
            .idVentaCatedra(UPDATED_ID_VENTA_CATEDRA)
            .fechaVenta(UPDATED_FECHA_VENTA)
            .precioTotal(UPDATED_PRECIO_TOTAL)
            .exitosa(UPDATED_EXITOSA)
            .descripcion(UPDATED_DESCRIPCION)
            .estadoSincronizacion(UPDATED_ESTADO_SINCRONIZACION);
        VentaDTO ventaDTO = ventaMapper.toDto(updatedVenta);

        restVentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ventaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVentaToMatchAllProperties(updatedVenta);
    }

    @Test
    @Transactional
    void putNonExistingVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ventaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ventaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVentaWithPatch() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the venta using partial update
        Venta partialUpdatedVenta = new Venta();
        partialUpdatedVenta.setId(venta.getId());

        partialUpdatedVenta.idVentaCatedra(UPDATED_ID_VENTA_CATEDRA).exitosa(UPDATED_EXITOSA).descripcion(UPDATED_DESCRIPCION);

        restVentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVenta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVenta))
            )
            .andExpect(status().isOk());

        // Validate the Venta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVentaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVenta, venta), getPersistedVenta(venta));
    }

    @Test
    @Transactional
    void fullUpdateVentaWithPatch() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the venta using partial update
        Venta partialUpdatedVenta = new Venta();
        partialUpdatedVenta.setId(venta.getId());

        partialUpdatedVenta
            .idVentaCatedra(UPDATED_ID_VENTA_CATEDRA)
            .fechaVenta(UPDATED_FECHA_VENTA)
            .precioTotal(UPDATED_PRECIO_TOTAL)
            .exitosa(UPDATED_EXITOSA)
            .descripcion(UPDATED_DESCRIPCION)
            .estadoSincronizacion(UPDATED_ESTADO_SINCRONIZACION);

        restVentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVenta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVenta))
            )
            .andExpect(status().isOk());

        // Validate the Venta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVentaUpdatableFieldsEquals(partialUpdatedVenta, getPersistedVenta(partialUpdatedVenta));
    }

    @Test
    @Transactional
    void patchNonExistingVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ventaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ventaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ventaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        venta.setId(longCount.incrementAndGet());

        // Create the Venta
        VentaDTO ventaDTO = ventaMapper.toDto(venta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ventaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Venta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVenta() throws Exception {
        // Initialize the database
        insertedVenta = ventaRepository.saveAndFlush(venta);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the venta
        restVentaMockMvc
            .perform(delete(ENTITY_API_URL_ID, venta.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ventaRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Venta getPersistedVenta(Venta venta) {
        return ventaRepository.findById(venta.getId()).orElseThrow();
    }

    protected void assertPersistedVentaToMatchAllProperties(Venta expectedVenta) {
        assertVentaAllPropertiesEquals(expectedVenta, getPersistedVenta(expectedVenta));
    }

    protected void assertPersistedVentaToMatchUpdatableProperties(Venta expectedVenta) {
        assertVentaAllUpdatablePropertiesEquals(expectedVenta, getPersistedVenta(expectedVenta));
    }
}
