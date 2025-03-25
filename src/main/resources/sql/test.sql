select * from coffee_shops;

select * from images;

select * from description_embedding;

select * from categories;

select
        cs1_0.id,
        cs1_0.addition_info,
        a1_0.id,
        a1_0.district,
        a1_0.google_map_url,
        a1_0.latitude,
        a1_0.longitude,
        a1_0.province,
        a1_0.ward,
        cs1_0.close_hour,
        cs1_0.cover_photo,
        cs1_0.description,
        cs1_0.menu_web_address,
        cs1_0.name,
        cs1_0.open_hour,
        cs1_0.phone_number,
        cs1_0.web_address 
    from
        coffee_shops cs1_0 
    left join
        address a1_0 
            on a1_0.id=cs1_0.address_id 
    where
        cs1_0.id= 2;


delete from description_embedding;

delete from images; 

delete from address;

delete from coffee_shops;

select
        cs1_0.id,
        cs1_0.addition_info,
        a1_0.id,
        a1_0.district,
        a1_0.google_map_url,
        a1_0.latitude,
        a1_0.longitude,
        a1_0.province,
        a1_0.ward,
        cs1_0.close_hour,
        cs1_0.cover_photo,
        cs1_0.description,
        cs1_0.menu_web_address,
        cs1_0.name,
        cs1_0.open_hour,
        cs1_0.phone_number,
        cs1_0.web_address 
    from
        coffee_shops cs1_0 
    left join
        address a1_0 
            on a1_0.id=cs1_0.address_id 
    where
        cs1_0.id= 2;