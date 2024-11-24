
INSERT INTO public.roles VALUES (1, 'ROLE_ADMIN');
INSERT INTO public.roles VALUES (2, 'ROLE_USER');

INSERT INTO public.users VALUES ('1', 'comp1@gmail.com', 'Comp1', '$2a$10$vKR.zWp2t.5zWc32vpLuK.JIkgKzt/EG0iniFtOTdPrNElOp.k6Nu', 'pera', 1);
INSERT INTO public.users VALUES ('2', 'comp2@gmail.com', 'Comp2', '$2a$10$vKR.zWp2t.5zWc32vpLuK.JIkgKzt/EG0iniFtOTdPrNElOp.k6Nu', 'mika', 2);

INSERT INTO public.clients(merchant_id, merchant_password, id)VALUES ('1', '1', '2');

INSERT INTO public.payment_method(id, api_url, name)VALUES ('66773f74-b720-44d9-a388-8b63bd4e39a8', 'aaa', 'card');
INSERT INTO public.payment_method(id, api_url, name)VALUES ('cf40fe23-a8a3-4a72-af6d-cd108eeb96d8', 'bbb', 'bitcoin');
INSERT INTO public.payment_method(id, api_url, name)VALUES ('ccbb3567-81fe-4b75-af4d-c1035ae137e7', 'ccc', 'paypal');

INSERT INTO public.client_payment_method(client_user_id, payment_method_id)VALUES ('2', '66773f74-b720-44d9-a388-8b63bd4e39a8');
INSERT INTO public.client_payment_method(client_user_id, payment_method_id)VALUES ('2', 'cf40fe23-a8a3-4a72-af6d-cd108eeb96d8');