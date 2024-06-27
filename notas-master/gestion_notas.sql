
-- Base de datos: `colegio`
--
CREATE DATABASE IF NOT EXISTS `gestion_notas` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `gestion_notas`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tbl_cursos`
--

CREATE TABLE `tbl_cursos` (
  `id_curso` int(11) NOT NULL,
  `nombre` varchar(200) DEFAULT NULL,
  `id_estado` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tbl_perfiles`
--

CREATE TABLE `tbl_perfiles` (
  `id_perfil` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tbl_perfiles`
--

INSERT INTO `tbl_perfiles` (`id_perfil`, `nombre`) VALUES
(1, 'RECTOR'),
(2, 'PROFESOR'),
(3, 'ALUMNO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tbl_tipo_doc`
--

CREATE TABLE `tbl_tipo_doc` (
  `id_tipo_doc` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tbl_tipo_doc`
--

INSERT INTO `tbl_tipo_doc` (`id_tipo_doc`, `nombre`) VALUES
(1, 'Cedula de Ciudadanía'),
(2, 'Cedula de Extranjería'),
(3, 'Tarjeta de Identidad');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tbl_usuarios`
--

CREATE TABLE `tbl_usuarios` (
  `id_usuario` int(11) NOT NULL,
  `nombres` varchar(200) DEFAULT NULL,
  `apellidos` varchar(200) DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `id_tipo_doc` int(11) DEFAULT NULL,
  `n_doc` varchar(12) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `correo` varchar(255) DEFAULT NULL,
  `telefono` varchar(12) DEFAULT NULL,
  `clave` text DEFAULT NULL,
  `id_perfil` int(11) DEFAULT NULL,
  `id_estado` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tbl_usuarios`
--

INSERT INTO `tbl_usuarios` (`id_usuario`, `nombres`, `apellidos`, `fecha_nacimiento`, `id_tipo_doc`, `n_doc`, `direccion`, `correo`, `telefono`, `clave`, `id_perfil`, `id_estado`) VALUES
(1, 'RECTOR', 'RECTOR', '2024-06-11', 2, '123123', 'Calle 1', 'DSASDA', 'SDASDA', '$2a$10$IM1e1qGcB7uZf8ngoP.LKOtQVRDZeBropL0IlT.tymBklThzVwws.', 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tbl_usuarios_cursos`
--

CREATE TABLE `tbl_usuarios_cursos` (
  `id_usuario_curso` int(11) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `id_curso` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `tbl_cursos`
--
ALTER TABLE `tbl_cursos`
  ADD PRIMARY KEY (`id_curso`);

--
-- Indices de la tabla `tbl_perfiles`
--
ALTER TABLE `tbl_perfiles`
  ADD PRIMARY KEY (`id_perfil`);

--
-- Indices de la tabla `tbl_tipo_doc`
--
ALTER TABLE `tbl_tipo_doc`
  ADD PRIMARY KEY (`id_tipo_doc`);

--
-- Indices de la tabla `tbl_usuarios`
--
ALTER TABLE `tbl_usuarios`
  ADD PRIMARY KEY (`id_usuario`),
  ADD KEY `tbl_tipo_doc_ibfk_1` (`id_tipo_doc`),
  ADD KEY `tbl_usuarios_ibfk_1` (`id_perfil`);

--
-- Indices de la tabla `tbl_usuarios_cursos`
--
ALTER TABLE `tbl_usuarios_cursos`
  ADD PRIMARY KEY (`id_usuario_curso`),
  ADD KEY `tbl_usuarios_cursos_ibfk_1` (`id_usuario`),
  ADD KEY `tbl_usuarios_cursos_ibfk_2` (`id_curso`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `tbl_cursos`
--
ALTER TABLE `tbl_cursos`
  MODIFY `id_curso` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `tbl_perfiles`
--
ALTER TABLE `tbl_perfiles`
  MODIFY `id_perfil` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tbl_tipo_doc`
--
ALTER TABLE `tbl_tipo_doc`
  MODIFY `id_tipo_doc` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tbl_usuarios`
--
ALTER TABLE `tbl_usuarios`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT de la tabla `tbl_usuarios_cursos`
--
ALTER TABLE `tbl_usuarios_cursos`
  MODIFY `id_usuario_curso` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `tbl_usuarios`
--
ALTER TABLE `tbl_usuarios`
  ADD CONSTRAINT `tbl_tipo_doc_ibfk_1` FOREIGN KEY (`id_tipo_doc`) REFERENCES `tbl_tipo_doc` (`id_tipo_doc`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tbl_usuarios_ibfk_1` FOREIGN KEY (`id_perfil`) REFERENCES `tbl_perfiles` (`id_perfil`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `tbl_usuarios_cursos`
--
ALTER TABLE `tbl_usuarios_cursos`
  ADD CONSTRAINT `tbl_usuarios_cursos_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `tbl_usuarios` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tbl_usuarios_cursos_ibfk_2` FOREIGN KEY (`id_curso`) REFERENCES `tbl_cursos` (`id_curso`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;