# ==============================================================================
# SketchUp Ruby Script: Luxury 3D Cutaway House Plan (16.4m x 14.7m)
# File: dining room.rb
# ==============================================================================

require 'sketchup.rb'

module Kelsoft
  module HousePlanBuilder

    def self.build
      model = Sketchup.active_model
      model.start_operation('Build 3D Cutaway House Plan', true)

      # Remove existing build if script re-executed
      existing = model.active_entities.grep(Sketchup::Group).find { |g| g.name == "House Plan Model" }
      existing.erase! if existing

      # Master Group
      master_group = model.active_entities.add_group
      master_group.name = "House Plan Model"
      entities = master_group.entities

      # Initialize Materials
      mats = create_materials(model)

      # Build Structural & Architectural Shell
      build_foundation_and_landscape(entities, mats)
      build_walls_with_doorways(entities, mats)

      # Build & Furnish Every Room
      build_living_atrium(entities, mats)
      build_kitchen_and_bar(entities, mats)
      build_dining_room(entities, mats)
      build_master_suite(entities, mats)
      build_bedroom_two_and_jack_jill(entities, mats)
      build_left_wing_bedrooms(entities, mats)
      build_laundry_and_baths(entities, mats)
      build_office_study(entities, mats)
      build_outdoor_decks_and_porch(entities, mats)

      # Optional Roof Structure (Hidden by default for 3D Cutaway floor plan view)
      build_roof_structure(entities, mats)

      # Position Camera View for Architectural 3D Cutaway View
      set_camera_view(model)

      model.commit_operation
      puts "================================================================="
      puts "  3D Cutaway House Plan Model Built Successfully!"
      puts "  Layout: 16.4m x 14.7m Custom House Plan"
      puts "  Includes: Central Atrium Lounge, Dining (4.0x3.2m), Kitchen Bar,"
      puts "  Master Suite, Walk-In Closet, Jack & Jill Bath, 3 Bedrooms,"
      puts "  Laundry (2.4x2.2m), Left Bath (3.0x2.5m), Office & Outdoor Decks."
      puts "================================================================="
      true
    end

    private

    # --------------------------------------------------------------------------
    # Material Generator
    # --------------------------------------------------------------------------
    def self.create_materials(model)
      mats = model.materials

      get_mat = ->(name, hex_color, alpha = 1.0) {
        mat = mats[name] || mats.add(name)
        mat.color = Sketchup::Color.new(hex_color)
        mat.alpha = alpha if alpha < 1.0
        mat
      }

      {
        ext_siding:  get_mat.call("3D_Timber_Siding", "#A07246"), # Warm Wood Siding
        wall_cream:  get_mat.call("3D_Wall_Cream", "#F5F2EB"),    # Interior Off-White
        floor_oak:   get_mat.call("3D_Honey_Oak", "#D4A373"),     # Hardwood Flooring
        floor_tile:  get_mat.call("3D_Ceramic_Tile", "#E5E0D8"),  # Tile Flooring
        deck_wood:   get_mat.call("3D_Teak_Deck", "#B87333"),     # Porch Decking
        lawn_grass:  get_mat.call("3D_Lawn_Grass", "#4CAF50"),    # Outdoor Lawn
        stone_base:  get_mat.call("3D_Stone_Paver", "#6C757D"),   # Foundation/Steps
        walnut:      get_mat.call("3D_Walnut_Wood", "#3E2723"),   # Wood Furniture
        sofa_fabric: get_mat.call("3D_Sofa_Fabric", "#D8CBB5"),   # Warm Upholstery
        bed_linen:   get_mat.call("3D_Bed_Linen", "#FAFAFA"),     # White Linen
        brass:       get_mat.call("3D_Brass_Gold", "#D4AC0D"),    # Brass/Gold Accents
        black_metal: get_mat.call("3D_Black_Metal", "#212529"),   # Metal Hardware
        glass:       get_mat.call("3D_Glass", "#D4E6F1", 0.35),   # Glass Panes
        plant_green: get_mat.call("3D_Foliage_Green", "#2E7D32"), # Greenery
        roof_slate:  get_mat.call("3D_Roof_Slate", "#343A40")     # Roof Tiles
      }
    end

    # --------------------------------------------------------------------------
    # Geometry Helpers (+Z Extrusion Guaranteed)
    # --------------------------------------------------------------------------
    def self.add_box(parent, origin, width, depth, height, material = nil)
      group = parent.add_group
      ent = group.entities
      x, y, z = origin
      pts = [
        [x, y, z],
        [x + width, y, z],
        [x + width, y + depth, z],
        [x, y + depth, z]
      ]
      face = ent.add_face(pts)
      if face
        face.reverse! if face.normal.z < 0 && height > 0
        face.pushpull(height)
      end
      group.material = material if material
      group
    end

    def self.add_cylinder(parent, center, radius, height, num_segments = 24, material = nil)
      group = parent.add_group
      ent = group.entities
      circle = ent.add_circle(center, [0, 0, 1], radius, num_segments)
      face = ent.add_face(circle)
      if face
        face.reverse! if face.normal.z < 0 && height > 0
        face.pushpull(height)
      end
      group.material = material if material
      group
    end

    def self.add_wall_seg(parent, x1, y1, x2, y2, thickness, height, material)
      group = parent.add_group
      ent = group.entities
      dx = x2 - x1
      dy = y2 - y1
      len = Math.hypot(dx, dy)
      return if len < 0.001

      nx = -dy / len * (thickness / 2.0)
      ny =  dx / len * (thickness / 2.0)

      pts = [
        [x1 + nx, y1 + ny, 0],
        [x2 + nx, y2 + ny, 0],
        [x2 - nx, y2 - ny, 0],
        [x1 - nx, y1 - ny, 0]
      ]
      face = ent.add_face(pts)
      if face
        face.reverse! if face.normal.z < 0 && height > 0
        face.pushpull(height)
      end
      group.material = material if material
      group
    end

    # --------------------------------------------------------------------------
    # 1. Foundation & Landscape Ground
    # --------------------------------------------------------------------------
    def self.build_foundation_and_landscape(entities, mats)
      # Lawn Grass Base
      add_box(entities, [-12.0.m, -10.0.m, -15.cm], 24.0.m, 20.0.m, 1.cm, mats[:lawn_grass])

      # House Foundation Slab (16.4m x 14.7m)
      add_box(entities, [-8.2.m, -6.2.m, -15.cm], 16.4.m, 14.7.m, 15.cm, mats[:stone_base])

      # Interior Hardwood Floor Slab
      add_box(entities, [-8.0.m, -6.0.m, 0], 16.0.m, 14.3.m, 0.5.cm, mats[:floor_oak])

      # Tile Flooring in Kitchen & Baths
      add_box(entities, [-1.8.m, 2.5.m, 0.6.cm], 3.6.m, 3.2.m, 0.2.cm, mats[:floor_tile]) # Kitchen
      add_box(entities, [2.0.m, -2.2.m, 0.6.cm], 3.0.m, 2.8.m, 0.2.cm, mats[:floor_tile]) # Jack & Jill Bath
      add_box(entities, [-7.2.m, -4.8.m, 0.6.cm], 3.0.m, 2.5.m, 0.2.cm, mats[:floor_tile]) # Left Bath
      add_box(entities, [5.8.m, 3.8.m, 0.6.cm], 1.9.m, 1.9.m, 0.2.cm, mats[:floor_tile]) # Master Bath
    end

    # --------------------------------------------------------------------------
    # 2. Architectural Walls with Doorways (Height = 2.0m for 3D Cutaway View)
    # --------------------------------------------------------------------------
    def self.build_walls_with_doorways(entities, mats)
      hw = 2.0.m # Cutaway Wall Height
      et = 18.cm # Exterior thickness
      it = 12.cm # Interior thickness

      # --- Exterior Walls ---
      # West Wall
      add_wall_seg(entities, -8.0.m, -6.0.m, -8.0.m, -4.5.m, et, hw, mats[:ext_siding])
      add_wall_seg(entities, -8.0.m, -3.2.m, -8.0.m, -1.2.m, et, hw, mats[:ext_siding])
      add_wall_seg(entities, -8.0.m, 0.0.m, -8.0.m, 3.2.m, et, hw, mats[:ext_siding])

      # Diagonal West Wall
      add_wall_seg(entities, -8.0.m, 3.2.m, -5.8.m, 5.7.m, et, hw, mats[:ext_siding])

      # North Rear Wall (Dining & Master Patio Openings)
      add_wall_seg(entities, -5.8.m, 5.7.m, -4.6.m, 5.7.m, et, hw, mats[:ext_siding])
      add_wall_seg(entities, -2.0.m, 5.7.m, 2.0.m, 5.7.m, et, hw, mats[:ext_siding])
      add_wall_seg(entities, 4.6.m, 5.7.m, 5.8.m, 5.7.m, et, hw, mats[:ext_siding])

      # Diagonal East Wall
      add_wall_seg(entities, 5.8.m, 5.7.m, 8.0.m, 3.2.m, et, hw, mats[:ext_siding])

      # East Wall
      add_wall_seg(entities, 8.0.m, 3.2.m, 8.0.m, 1.5.m, et, hw, mats[:ext_siding])
      add_wall_seg(entities, 8.0.m, -0.2.m, 8.0.m, -2.5.m, et, hw, mats[:ext_siding])
      add_wall_seg(entities, 8.0.m, -3.8.m, 8.0.m, -6.0.m, et, hw, mats[:ext_siding])

      # South Walls (Front Entry Opening)
      add_wall_seg(entities, 8.0.m, -6.0.m, 1.5.m, -6.0.m, et, hw, mats[:ext_siding])
      add_wall_seg(entities, -1.5.m, -6.0.m, -8.0.m, -6.0.m, et, hw, mats[:ext_siding])

      # --- Interior Partition Walls with Clean Door Openings ---
      # Left Wing Partitions
      add_wall_seg(entities, -8.0.m, -2.3.m, -5.0.m, -2.3.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, -4.0.m, -2.3.m, -1.8.m, -2.3.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, -4.2.m, -6.0.m, -4.2.m, -2.3.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, -8.0.m, 0.5.m, -5.0.m, 0.5.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, -4.0.m, 0.5.m, -1.8.m, 0.5.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, -5.8.m, 0.5.m, -5.8.m, 3.2.m, it, hw, mats[:wall_cream])

      # Right Wing Partitions
      add_wall_seg(entities, 1.8.m, -6.0.m, 1.8.m, -3.5.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, 1.8.m, -2.5.m, 1.8.m, 5.7.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, 1.8.m, -1.8.m, 4.5.m, -1.8.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, 5.5.m, -1.8.m, 8.0.m, -1.8.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, 5.0.m, -6.0.m, 5.0.m, -1.8.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, 1.8.m, 2.0.m, 5.0.m, 2.0.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, 6.0.m, 2.0.m, 8.0.m, 2.0.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, 5.8.m, 2.0.m, 5.8.m, 5.7.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, 5.8.m, 3.8.m, 8.0.m, 3.8.m, it, hw, mats[:wall_cream])
      add_wall_seg(entities, -1.8.m, 2.0.m, -1.8.m, 5.7.m, it, hw, mats[:wall_cream])
    end

    # --------------------------------------------------------------------------
    # 3. Central Living Atrium (Circular Seating Pit)
    # --------------------------------------------------------------------------
    def self.build_living_atrium(entities, mats)
      atrium_grp = entities.add_group
      a_ent = atrium_grp.entities

      # Round Rug
      add_cylinder(a_ent, [0, -1.0.m, 0.5.cm], 1.6.m, 0.5.cm, 32, mats[:sofa_fabric])

      # Circular Sectional Sofa
      add_cylinder(a_ent, [0, -1.0.m, 1.cm], 1.5.m, 42.cm, 32, mats[:sofa_fabric])
      add_cylinder(a_ent, [0, -1.0.m, 1.cm], 0.9.m, 45.cm, 32, mats[:floor_oak])

      # Center Fire Pit / Coffee Table
      add_cylinder(a_ent, [0, -1.0.m, 1.cm], 45.cm, 35.cm, 24, mats[:walnut])
      add_cylinder(a_ent, [0, -1.0.m, 36.cm], 30.cm, 4.cm, 24, mats[:brass])

      # Decorative Planters
      add_cylinder(a_ent, [-1.5.m, 1.0.m, 0], 22.cm, 48.cm, 16, mats[:wall_cream])
      add_box(a_ent, [-1.7.m, 0.8.m, 48.cm], 40.cm, 40.cm, 60.cm, mats[:plant_green])
      add_cylinder(a_ent, [1.5.m, 1.0.m, 0], 22.cm, 48.cm, 16, mats[:wall_cream])
      add_box(a_ent, [1.3.m, 0.8.m, 48.cm], 40.cm, 40.cm, 60.cm, mats[:plant_green])
    end

    # --------------------------------------------------------------------------
    # 4. Kitchen & Curved Bar Island
    # --------------------------------------------------------------------------
    def self.build_kitchen_and_bar(entities, mats)
      kitch_grp = entities.add_group
      k_ent = kitch_grp.entities

      add_box(k_ent, [-1.5.m, 2.6.m, 0], 3.0.m, 0.7.m, 90.cm, mats[:walnut])
      add_box(k_ent, [-1.55.m, 2.58.m, 90.cm], 3.1.m, 0.74.m, 4.cm, mats[:wall_cream])

      [-0.8.m, 0.0.m, 0.8.m].each do |bx|
        add_cylinder(k_ent, [bx, 2.2.m, 0], 18.cm, 65.cm, 16, mats[:brass])
        add_cylinder(k_ent, [bx, 2.2.m, 65.cm], 20.cm, 5.cm, 16, mats[:sofa_fabric])
      end

      add_box(k_ent, [-1.7.m, 5.0.m, 0], 3.4.m, 0.65.m, 90.cm, mats[:walnut])
      add_box(k_ent, [-1.75.m, 4.98.m, 90.cm], 3.5.m, 0.69.m, 4.cm, mats[:wall_cream])
      add_box(k_ent, [-0.9.m, 5.1.m, 94.cm], 50.cm, 40.cm, 2.cm, mats[:black_metal])
      add_box(k_ent, [0.4.m, 5.1.m, 94.cm], 60.cm, 45.cm, 2.cm, mats[:brass])
    end

    # --------------------------------------------------------------------------
    # 5. Dining Room (4.0m x 3.2m)
    # --------------------------------------------------------------------------
    def self.build_dining_room(entities, mats)
      dining_grp = entities.add_group
      d_ent = dining_grp.entities

      add_box(d_ent, [-5.2.m, 2.4.m, 0.8.cm], 2.8.m, 2.2.m, 0.5.cm, mats[:sofa_fabric])
      add_cylinder(d_ent, [-3.8.m, 3.5.m, 0], 6.cm, 72.cm, 16, mats[:brass])
      add_cylinder(d_ent, [-3.8.m, 3.5.m, 72.cm], 85.cm, 4.cm, 32, mats[:walnut])

      6.times do |i|
        angle = i * (2.0 * Math::PI / 6.0)
        cx = -3.8.m + (1.1.m * Math.cos(angle))
        cy =  3.5.m + (1.1.m * Math.sin(angle))
        place_chair(d_ent, cx, cy, 0, (i * 60) + 180, mats)
      end

      add_box(d_ent, [-5.7.m, 2.6.m, 0], 0.45.m, 1.8.m, 82.cm, mats[:walnut])
      add_cylinder(d_ent, [-5.5.m, 3.5.m, 82.cm], 9.cm, 28.cm, 16, mats[:wall_cream])
      add_box(d_ent, [-5.6.m, 3.4.m, 1.1.m], 20.cm, 20.cm, 25.cm, mats[:plant_green])
      add_cylinder(d_ent, [-3.8.m, 3.5.m, 2.0.m], 30.cm, 4.cm, 24, mats[:brass])
    end

    def self.place_chair(parent, pos_x, pos_y, pos_z, rotation_deg, mats)
      chair_grp = parent.add_group
      c_ent = chair_grp.entities

      add_box(c_ent, [-20.cm, -20.cm, 40.cm], 40.cm, 40.cm, 3.cm, mats[:walnut])
      add_box(c_ent, [-21.cm, -21.cm, 43.cm], 42.cm, 42.cm, 4.cm, mats[:sofa_fabric])

      [-16.cm, 16.cm].each do |lx|
        [-16.cm, 16.cm].each do |ly|
          add_cylinder(c_ent, [lx, ly, 0], 1.5.cm, 40.cm, 12, mats[:walnut])
        end
      end

      add_cylinder(c_ent, [-18.cm, -18.cm, 40.cm], 1.5.cm, 44.cm, 12, mats[:walnut])
      add_cylinder(c_ent, [ 18.cm, -18.cm, 40.cm], 1.5.cm, 44.cm, 12, mats[:walnut])
      add_box(c_ent, [-19.cm, -20.cm, 68.cm], 38.cm, 3.cm, 16.cm, mats[:sofa_fabric])

      t_rot = Geom::Transformation.rotation([0, 0, 0], [0, 0, 1], rotation_deg.degrees)
      t_trans = Geom::Transformation.translation([pos_x, pos_y, pos_z])
      chair_grp.transform!(t_trans * t_rot)
    end

    # --------------------------------------------------------------------------
    # 6. Master Suite (4.0m x 3.7m), Walk-in Closet & En-Suite Bath
    # --------------------------------------------------------------------------
    def self.build_master_suite(entities, mats)
      m_grp = entities.add_group
      m_ent = m_grp.entities

      add_box(m_ent, [2.8.m, 2.8.m, 0], 2.0.m, 2.1.m, 35.cm, mats[:walnut])
      add_box(m_ent, [2.82.m, 2.82.m, 35.cm], 1.96.m, 2.06.m, 20.cm, mats[:bed_linen])
      add_box(m_ent, [2.75.m, 4.85.m, 0], 2.1.m, 0.1.m, 1.2.m, mats[:walnut])
      add_box(m_ent, [3.0.m, 4.4.m, 55.cm], 65.cm, 40.cm, 12.cm, mats[:bed_linen])
      add_box(m_ent, [3.9.m, 4.4.m, 55.cm], 65.cm, 40.cm, 12.cm, mats[:bed_linen])

      add_box(m_ent, [2.1.m, 4.3.m, 0], 55.cm, 45.cm, 50.cm, mats[:walnut])
      add_box(m_ent, [5.0.m, 4.3.m, 0], 55.cm, 45.cm, 50.cm, mats[:walnut])

      # Closet & Bath
      add_box(m_ent, [5.9.m, 2.1.m, 0], 1.7.m, 0.5.m, 1.8.m, mats[:walnut])
      add_box(m_ent, [7.1.m, 2.6.m, 0], 0.5.m, 1.1.m, 1.8.m, mats[:walnut])
      add_box(m_ent, [6.0.m, 5.0.m, 0], 1.4.m, 0.55.m, 85.cm, mats[:wall_cream])
      add_box(m_ent, [6.1.m, 4.98.m, 1.1.m], 1.2.m, 0.05.m, 80.cm, mats[:brass])
      add_box(m_ent, [7.0.m, 3.9.m, 0], 0.9.m, 0.9.m, 1.8.m, mats[:glass])
    end

    # --------------------------------------------------------------------------
    # 7. Bedroom 2 (3.5m x 3.2m) & Jack-and-Jill Bath (3.0m x 2.8m)
    # --------------------------------------------------------------------------
    def self.build_bedroom_two_and_jack_jill(entities, mats)
      b2_grp = entities.add_group
      b_ent = b2_grp.entities

      add_box(b_ent, [4.4.m, -4.8.m, 0], 1.6.m, 2.0.m, 32.cm, mats[:walnut])
      add_box(b_ent, [4.42.m, -4.78.m, 32.cm], 1.56.m, 1.96.m, 18.cm, mats[:bed_linen])
      add_box(b_ent, [4.35.m, -2.85.m, 0], 1.7.m, 0.1.m, 1.1.m, mats[:walnut])
      add_box(b_ent, [7.2.m, -5.8.m, 0], 0.6.m, 1.8.m, 1.8.m, mats[:walnut])

      add_box(b_ent, [2.1.m, -1.6.m, 0], 1.5.m, 0.55.m, 85.cm, mats[:wall_cream])
      add_box(b_ent, [4.0.m, -2.0.m, 0], 0.8.m, 1.5.m, 55.cm, mats[:wall_cream])
    end

    # --------------------------------------------------------------------------
    # 8. Left Wing Bedrooms (Bedrooms 3 & 4)
    # --------------------------------------------------------------------------
    def self.build_left_wing_bedrooms(entities, mats)
      lw_grp = entities.add_group
      l_ent = lw_grp.entities

      add_box(l_ent, [-6.8.m, -4.8.m, 0], 1.6.m, 2.0.m, 32.cm, mats[:walnut])
      add_box(l_ent, [-6.78.m, -4.78.m, 32.cm], 1.56.m, 1.96.m, 18.cm, mats[:bed_linen])

      add_box(l_ent, [-6.8.m, -1.8.m, 0], 1.6.m, 2.0.m, 32.cm, mats[:walnut])
      add_box(l_ent, [-6.78.m, -1.78.m, 32.cm], 1.56.m, 1.96.m, 18.cm, mats[:bed_linen])
    end

    # --------------------------------------------------------------------------
    # 9. Laundry Room (2.4m x 2.2m) & Left Bath (3.0m x 2.5m)
    # --------------------------------------------------------------------------
    def self.build_laundry_and_baths(entities, mats)
      util_grp = entities.add_group
      u_ent = util_grp.entities

      add_box(u_ent, [-3.8.m, -4.6.m, 0], 65.cm, 65.cm, 90.cm, mats[:wall_cream])
      add_box(u_ent, [-3.1.m, -4.6.m, 0], 65.cm, 65.cm, 90.cm, mats[:black_metal])
      add_box(u_ent, [-2.4.m, -4.6.m, 0], 80.cm, 60.cm, 88.cm, mats[:wall_cream])

      add_box(u_ent, [-6.8.m, -2.5.m, 0], 1.2.m, 0.55.m, 85.cm, mats[:wall_cream])
      add_box(u_ent, [-5.2.m, -2.5.m, 0], 0.9.m, 0.9.m, 1.8.m, mats[:glass])
    end

    # --------------------------------------------------------------------------
    # 10. Home Office / Study
    # --------------------------------------------------------------------------
    def self.build_office_study(entities, mats)
      off_grp = entities.add_group
      o_ent = off_grp.entities

      add_box(o_ent, [-7.2.m, 1.2.m, 0], 1.6.m, 0.8.m, 76.cm, mats[:walnut])
      add_cylinder(o_ent, [-6.4.m, 0.7.m, 0], 25.cm, 85.cm, 16, mats[:black_metal])
      add_box(o_ent, [-7.8.m, 0.6.m, 0], 0.4.m, 1.8.m, 1.8.m, mats[:walnut])
    end

    # --------------------------------------------------------------------------
    # 11. Outdoor Covered Decks & Entrance Porch
    # --------------------------------------------------------------------------
    def self.build_outdoor_decks_and_porch(entities, mats)
      deck_grp = entities.add_group
      d_ent = deck_grp.entities

      # Front Porch
      add_cylinder(d_ent, [0, -6.0.m, 0], 2.8.m, 10.cm, 32, mats[:deck_wood])
      add_cylinder(d_ent, [0, -6.0.m, -10.cm], 3.2.m, 10.cm, 32, mats[:stone_base])

      [-2.2.m, 0.0.m, 2.2.m].each do |cx|
        add_cylinder(d_ent, [cx, -7.5.m, 0], 12.cm, 2.0.m, 16, mats[:ext_siding])
      end

      # Rear Deck
      add_cylinder(d_ent, [0, 5.7.m, 0], 3.5.m, 10.cm, 32, mats[:deck_wood])
      add_box(d_ent, [-1.2.m, 6.2.m, 10.cm], 1.6.m, 0.9.m, 75.cm, mats[:walnut])
      4.times do |i|
        angle = i * (2.0 * Math::PI / 4.0)
        cx = 0.0.m + (1.2.m * Math.cos(angle))
        cy = 6.6.m + (1.2.m * Math.sin(angle))
        add_box(d_ent, [cx - 20.cm, cy - 20.cm, 10.cm], 40.cm, 40.cm, 45.cm, mats[:sofa_fabric])
      end
    end

    # --------------------------------------------------------------------------
    # 12. Optional Roof Structure (Stored in separate group)
    # --------------------------------------------------------------------------
    def self.build_roof_structure(entities, mats)
      roof_grp = entities.add_group
      roof_grp.name = "Roof Structure (Toggle Hide/Show)"
      r_ent = roof_grp.entities
      rz = 2.0.m

      add_box(r_ent, [-8.4.m, -6.2.m, rz], 16.8.m, 14.9.m, 15.cm, mats[:ext_siding])
      add_box(r_ent, [-8.2.m, -6.0.m, rz + 15.cm], 16.4.m, 14.5.m, 1.0.m, mats[:roof_slate])

      # Hide roof by default so the 3D cutaway floor plan interior is 100% visible
      roof_grp.hidden = true
    end

    # --------------------------------------------------------------------------
    # 13. Aerial Isometric Camera View
    # --------------------------------------------------------------------------
    def self.set_camera_view(model)
      view = model.active_view
      eye = [0.0.m, -18.0.m, 15.0.m]
      target = [0.0.m, 0.0.m, 1.0.m]
      up = [0, 0, 1]
      camera = Sketchup::Camera.new(eye, target, up)
      view.camera = camera
    end

  end
end

# Automatically trigger execution when loaded in SketchUp Ruby Console
Kelsoft::HousePlanBuilder.build
