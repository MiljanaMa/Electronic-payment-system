
INSERT INTO public.roles VALUES (1, 'ROLE_ADMIN');
INSERT INTO public.roles VALUES (2, 'ROLE_USER');

INSERT INTO public.users VALUES ('1', 'Pera', 'Peric', '$2a$10$vKR.zWp2t.5zWc32vpLuK.JIkgKzt/EG0iniFtOTdPrNElOp.k6Nu', 'pera', 1);
INSERT INTO public.users VALUES ('2', 'Mika', 'Mikic', '$2a$10$vKR.zWp2t.5zWc32vpLuK.JIkgKzt/EG0iniFtOTdPrNElOp.k6Nu', 'mika', 2);

INSERT INTO public.products VALUES ('1', 'Download up to 300 Mbps and upload up to 150 Mbps', 'Yettel Net M', 2699, 'INTERNET');

INSERT INTO public.bundles VALUES ('1', 'NET + TV + MOBILE', 'Yettel Everything M', 5500);

INSERT INTO public.bundle_products VALUES ('1', '1', '1');

