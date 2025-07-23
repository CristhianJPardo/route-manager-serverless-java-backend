-- -----------------------------------------------------
-- 1. Tabla de clientes
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS clients (
  id SERIAL PRIMARY KEY,
  firstname TEXT NOT NULL,
  lastname TEXT NOT NULL,
  dni VARCHAR(50) NOT NULL UNIQUE,
  username VARCHAR(255) NOT NULL UNIQUE,
  address TEXT,
  whatsapp VARCHAR(20),
  phone VARCHAR(20),
  email VARCHAR(255) NOT NULL UNIQUE,
  password TEXT NOT NULL,
  locality VARCHAR(100),
  neighborhood VARCHAR(100),
  latitude DOUBLE PRECISION DEFAULT 0,
  longitude DOUBLE PRECISION DEFAULT 0,
  role VARCHAR(50) NOT NULL,
  registration_date DATE NOT NULL,
  precedence INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT TRUE,
  credentials_non_expired BOOLEAN DEFAULT TRUE,
  account_non_expired BOOLEAN DEFAULT TRUE,
  account_non_locked BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- -----------------------------------------------------
-- 2. Tabla de conductores (drivers)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS drivers (
  id SERIAL PRIMARY KEY,
  firstname TEXT NOT NULL,
  lastname TEXT NOT NULL,
  dni VARCHAR(50) NOT NULL UNIQUE,
  username VARCHAR(255) NOT NULL UNIQUE,
  license_category VARCHAR(10),
  license_expiration DATE,
  address TEXT,
  whatsapp VARCHAR(20),
  phone VARCHAR(20),
  email VARCHAR(255) NOT NULL UNIQUE,
  password TEXT NOT NULL,
  locality VARCHAR(100),
  neighborhood VARCHAR(100),
  latitude DOUBLE PRECISION DEFAULT 0,
  longitude DOUBLE PRECISION DEFAULT 0,
  role VARCHAR(50) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- -----------------------------------------------------
-- 3. Tabla de managers (administradores)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS managers (
  id SERIAL PRIMARY KEY,
  firstname TEXT NOT NULL,
  lastname TEXT NOT NULL,
  dni VARCHAR(50) NOT NULL UNIQUE,
  username VARCHAR(255) NOT NULL UNIQUE,
  address TEXT,
  whatsapp VARCHAR(20),
  phone VARCHAR(20),
  email VARCHAR(255) NOT NULL UNIQUE,
  password TEXT NOT NULL,
  locality VARCHAR(100),
  neighborhood VARCHAR(100),
  latitude DOUBLE PRECISION DEFAULT 0,
  longitude DOUBLE PRECISION DEFAULT 0,
  role VARCHAR(50) NOT NULL,
  hiring_date DATE NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- -----------------------------------------------------
-- 4. Tabla de rutas (routes)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS routes (
  id SERIAL PRIMARY KEY,
  driver_id INTEGER NOT NULL
    REFERENCES drivers(id)
    ON DELETE CASCADE,
  start_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- -----------------------------------------------------
-- 5. Tabla de paradas de rutas (route_stops)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS route_stops (
  id SERIAL PRIMARY KEY,
  route_id INTEGER NOT NULL
    REFERENCES routes(id)
    ON DELETE CASCADE,
  client_id INTEGER NOT NULL
    REFERENCES clients(id)
    ON DELETE CASCADE,
  pickup BOOLEAN NOT NULL,
  stop_order INTEGER NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
