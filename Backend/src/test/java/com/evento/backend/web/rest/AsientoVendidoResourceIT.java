package com.evento.backend.web.rest;

import static com.evento.backend.domain.AsientoVendidoAsserts.*;
import static com.evento.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.evento.backend.IntegrationTest;
import com.evento.backend.domain.AsientoVendido;
import com.evento.backend.domain.Venta;
import com.evento.backend.repository.AsientoVendidoRepository;
import com.evento.backend.service.dto.AsientoVendidoDTO;
import com.evento.backend.service.mapper.AsientoVendidoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AsientoVendidoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AsientoVendidoResourceIT {

    private static final Integer DEFAULT_FILA = 1;
    private static final Integer UPDATED_FILA = 2;

    private static final Integer DEFAULT_COLUMNA = 1;
    private static final Integer UPDATED_COLUMNA = 2;

    private static final String DEFAULT_NOMBRE_PERSONA = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_PERSONA = "BBBBBBBBBB";

    private static final String DEFAULT_ESTADO = "AAAAAAAAAA";
    private static final String UPDATED_ESTADO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/asiento-vendidos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AsientoVendidoRepository asientoVendidoRepository;

    @Autowired
    private AsientoVendidoMapper asientoVendidoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAsientoVendidoMockMvc;

    private AsientoVendido asientoVendido;

    private AsientoVendido insertedAsientoVendido;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AsientoVendido createEntity(EntityManager em) {
        AsientoVendido asientoVendido = new AsientoVendido()
            .fila(DEFAULT_FILA)
            .columna(DEFAULT_COLUMNA)
            .nombrePersona(DEFAULT_NOMBRE_PERSONA)
            .estado(DEFAULT_ESTADO);
        // Add required entity
        Venta venta;
        if (TestUtil.findAll(em, Venta.class).isEmpty()) {
            venta = VentaResourceIT.createEntity(em);
            em.persist(venta);
            em.flush();
        } else {
            venta = TestUtil.findAll(em, Venta.class).get(0);
        }
        asientoVendido.setVenta(venta);
        return asientoVendido;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AsientoVendido createUpdatedEntity(EntityManager em) {
        AsientoVendido updatedAsientoVendido = new AsientoVendido()
            .fila(UPDATED_FILA)
            .columna(UPDATED_COLUMNA)
            .nombrePersona(UPDATED_NOMBRE_PERSONA)
            .estado(UPDATED_ESTADO);
        // Add required entity
        Venta venta;
        if (TestUtil.findAll(em, Venta.class).isEmpty()) {
            venta = VentaResourceIT.createUpdatedEntity(em);
            em.persist(venta);
            em.flush();
        } else {
            venta = TestUtil.findAll(em, Venta.class).get(0);
        }
        updatedAsientoVendido.setVenta(venta);
        return updatedAsientoVendido;
    }

    @BeforeEach
    void initTest() {
        asientoVendido = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAsientoVendido != null) {
            asientoVendidoRepository.delete(insertedAsientoVendido);
            insertedAsientoVendido = null;
        }
    }

    @Test
    @Transactional
    void createAsientoVendido() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AsientoVendido
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(asientoVendido);
        var returnedAsientoVendidoDTO = om.readValue(
            restAsientoVendidoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asientoVendidoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AsientoVendidoDTO.class
        );

        // Validate the AsientoVendido in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAsientoVendido = asientoVendidoMapper.toEntity(returnedAsientoVendidoDTO);
        assertAsientoVendidoUpdatableFieldsEquals(returnedAsientoVendido, getPersistedAsientoVendido(returnedAsientoVendido));

        insertedAsientoVendido = returnedAsientoVendido;
    }

    @Test
    @Transactional
    void createAsientoVendidoWithExistingId() throws Exception {
        // Create the AsientoVendido with an existing ID
        asientoVendido.setId(1L);
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(asientoVendido);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAsientoVendidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asientoVendidoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AsientoVendido in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFilaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        asientoVendido.setFila(null);

        // Create the AsientoVendido, which fails.
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(asientoVendido);

        restAsientoVendidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asientoVendidoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkColumnaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        asientoVendido.setColumna(null);

        // Create the AsientoVendido, which fails.
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(asientoVendido);

        restAsientoVendidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asientoVendidoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNombrePersonaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        asientoVendido.setNombrePersona(null);

        // Create the AsientoVendido, which fails.
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(asientoVendido);

        restAsientoVendidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asientoVendidoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAsientoVendidos() throws Exception {
        // Initialize the database
        insertedAsientoVendido = asientoVendidoRepository.saveAndFlush(asientoVendido);

        // Get all the asientoVendidoList
        restAsientoVendidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(asientoVendido.getId().intValue())))
            .andExpect(jsonPath("$.[*].fila").value(hasItem(DEFAULT_FILA)))
            .andExpect(jsonPath("$.[*].columna").value(hasItem(DEFAULT_COLUMNA)))
            .andExpect(jsonPath("$.[*].nombrePersona").value(hasItem(DEFAULT_NOMBRE_PERSONA)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO)));
    }

    @Test
    @Transactional
    void getAsientoVendido() throws Exception {
        // Initialize the database
        insertedAsientoVendido = asientoVendidoRepository.saveAndFlush(asientoVendido);

        // Get the asientoVendido
        restAsientoVendidoMockMvc
            .perform(get(ENTITY_API_URL_ID, asientoVendido.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(asientoVendido.getId().intValue()))
            .andExpect(jsonPath("$.fila").value(DEFAULT_FILA))
            .andExpect(jsonPath("$.columna").value(DEFAULT_COLUMNA))
            .andExpect(jsonPath("$.nombrePersona").value(DEFAULT_NOMBRE_PERSONA))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO));
    }

    @Test
    @Transactional
    void getNonExistingAsientoVendido() throws Exception {
        // Get the asientoVendido
        restAsientoVendidoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAsientoVendido() throws Exception {
        // Initialize the database
        insertedAsientoVendido = asientoVendidoRepository.saveAndFlush(asientoVendido);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the asientoVendido
        AsientoVendido updatedAsientoVendido = asientoVendidoRepository.findById(asientoVendido.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAsientoVendido are not directly saved in db
        em.detach(updatedAsientoVendido);
        updatedAsientoVendido.fila(UPDATED_FILA).columna(UPDATED_COLUMNA).nombrePersona(UPDATED_NOMBRE_PERSONA).estado(UPDATED_ESTADO);
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(updatedAsientoVendido);

        restAsientoVendidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, asientoVendidoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(asientoVendidoDTO))
            )
            .andExpect(status().isOk());

        // Validate the AsientoVendido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAsientoVendidoToMatchAllProperties(updatedAsientoVendido);
    }

    @Test
    @Transactional
    void putNonExistingAsientoVendido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoVendido.setId(longCount.incrementAndGet());

        // Create the AsientoVendido
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(asientoVendido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAsientoVendidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, asientoVendidoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(asientoVendidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AsientoVendido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAsientoVendido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoVendido.setId(longCount.incrementAndGet());

        // Create the AsientoVendido
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(asientoVendido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAsientoVendidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(asientoVendidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AsientoVendido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAsientoVendido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoVendido.setId(longCount.incrementAndGet());

        // Create the AsientoVendido
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(asientoVendido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAsientoVendidoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asientoVendidoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AsientoVendido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAsientoVendidoWithPatch() throws Exception {
        // Initialize the database
        insertedAsientoVendido = asientoVendidoRepository.saveAndFlush(asientoVendido);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the asientoVendido using partial update
        AsientoVendido partialUpdatedAsientoVendido = new AsientoVendido();
        partialUpdatedAsientoVendido.setId(asientoVendido.getId());

        partialUpdatedAsientoVendido.columna(UPDATED_COLUMNA).nombrePersona(UPDATED_NOMBRE_PERSONA);

        restAsientoVendidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAsientoVendido.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAsientoVendido))
            )
            .andExpect(status().isOk());

        // Validate the AsientoVendido in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAsientoVendidoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAsientoVendido, asientoVendido),
            getPersistedAsientoVendido(asientoVendido)
        );
    }

    @Test
    @Transactional
    void fullUpdateAsientoVendidoWithPatch() throws Exception {
        // Initialize the database
        insertedAsientoVendido = asientoVendidoRepository.saveAndFlush(asientoVendido);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the asientoVendido using partial update
        AsientoVendido partialUpdatedAsientoVendido = new AsientoVendido();
        partialUpdatedAsientoVendido.setId(asientoVendido.getId());

        partialUpdatedAsientoVendido
            .fila(UPDATED_FILA)
            .columna(UPDATED_COLUMNA)
            .nombrePersona(UPDATED_NOMBRE_PERSONA)
            .estado(UPDATED_ESTADO);

        restAsientoVendidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAsientoVendido.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAsientoVendido))
            )
            .andExpect(status().isOk());

        // Validate the AsientoVendido in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAsientoVendidoUpdatableFieldsEquals(partialUpdatedAsientoVendido, getPersistedAsientoVendido(partialUpdatedAsientoVendido));
    }

    @Test
    @Transactional
    void patchNonExistingAsientoVendido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoVendido.setId(longCount.incrementAndGet());

        // Create the AsientoVendido
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(asientoVendido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAsientoVendidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, asientoVendidoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(asientoVendidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AsientoVendido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAsientoVendido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoVendido.setId(longCount.incrementAndGet());

        // Create the AsientoVendido
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(asientoVendido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAsientoVendidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(asientoVendidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AsientoVendido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAsientoVendido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoVendido.setId(longCount.incrementAndGet());

        // Create the AsientoVendido
        AsientoVendidoDTO asientoVendidoDTO = asientoVendidoMapper.toDto(asientoVendido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAsientoVendidoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(asientoVendidoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AsientoVendido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAsientoVendido() throws Exception {
        // Initialize the database
        insertedAsientoVendido = asientoVendidoRepository.saveAndFlush(asientoVendido);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the asientoVendido
        restAsientoVendidoMockMvc
            .perform(delete(ENTITY_API_URL_ID, asientoVendido.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return asientoVendidoRepository.count();
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

    protected AsientoVendido getPersistedAsientoVendido(AsientoVendido asientoVendido) {
        return asientoVendidoRepository.findById(asientoVendido.getId()).orElseThrow();
    }

    protected void assertPersistedAsientoVendidoToMatchAllProperties(AsientoVendido expectedAsientoVendido) {
        assertAsientoVendidoAllPropertiesEquals(expectedAsientoVendido, getPersistedAsientoVendido(expectedAsientoVendido));
    }

    protected void assertPersistedAsientoVendidoToMatchUpdatableProperties(AsientoVendido expectedAsientoVendido) {
        assertAsientoVendidoAllUpdatablePropertiesEquals(expectedAsientoVendido, getPersistedAsientoVendido(expectedAsientoVendido));
    }
}
